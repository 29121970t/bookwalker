#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
BOOK_COUNT="${BOOK_COUNT:-100}"
OTHER_ENTITY_COUNT="${OTHER_ENTITY_COUNT:-10}"
ORDER_COUNT="${ORDER_COUNT:-10}"
ORDER_BATCH_SIZE="${ORDER_BATCH_SIZE:-20}"
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@bookwalker.app}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-admin123}"
CURL_OPTS=(
  --silent
  --show-error
  --fail-with-body
)

declare -a genre_ids=()
declare -a tag_ids=()
declare -a publisher_ids=()
declare -a author_ids=()
declare -a client_ids=()
declare -a book_ids=()
ADMIN_TOKEN=""

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

extract_first_match() {
  local pattern="$1"
  sed -n "s/${pattern}/\\1/p" | head -n 1
}

extract_id() {
  extract_first_match '.*"id"[[:space:]]*:[[:space:]]*\([0-9][0-9]*\).*'
}

extract_token() {
  extract_first_match '.*"token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*'
}

json_escape() {
  local value="$1"
  value="${value//\\/\\\\}"
  value="${value//\"/\\\"}"
  value="${value//$'\n'/\\n}"
  printf '%s' "$value"
}

post_json() {
  local endpoint="$1"
  local payload="$2"
  shift 2 || true
  curl "${CURL_OPTS[@]}" \
    --header "Content-Type: application/json" \
    "$@" \
    --request POST \
    --data "$payload" \
    "${BASE_URL}${endpoint}"
}

get_json() {
  local endpoint="$1"
  shift || true
  curl "${CURL_OPTS[@]}" "$@" "${BASE_URL}${endpoint}"
}

check_api() {
  echo "Checking API availability at ${BASE_URL}..."
  curl "${CURL_OPTS[@]}" "${BASE_URL}/api-docs" >/dev/null
}

login_as_admin() {
  echo "Authenticating as admin..."
  local payload response
  payload="$(printf '{"email":"%s","password":"%s"}' \
    "$(json_escape "$ADMIN_EMAIL")" \
    "$(json_escape "$ADMIN_PASSWORD")")"
  response="$(post_json "/auth/login" "$payload")"
  ADMIN_TOKEN="$(printf '%s' "$response" | extract_token)"
  if [[ -z "$ADMIN_TOKEN" ]]; then
    echo "Failed to retrieve admin token from /auth/login" >&2
    exit 1
  fi
}

with_admin_auth() {
  printf '%s\n%s\n' "--header" "Authorization: Bearer ${ADMIN_TOKEN}"
}

post_admin_json() {
  local endpoint="$1"
  local payload="$2"
  curl "${CURL_OPTS[@]}" \
    --header "Content-Type: application/json" \
    --header "Authorization: Bearer ${ADMIN_TOKEN}" \
    --request POST \
    --data "$payload" \
    "${BASE_URL}${endpoint}"
}

get_admin_json() {
  local endpoint="$1"
  curl "${CURL_OPTS[@]}" \
    --header "Authorization: Bearer ${ADMIN_TOKEN}" \
    "${BASE_URL}${endpoint}"
}

create_genres() {
  echo "Creating genres..."
  local i response id payload
  for ((i = 1; i <= OTHER_ENTITY_COUNT; i++)); do
    payload="$(printf '{"name":"Genre %d","description":"Sample genre %d"}' "$i" "$i")"
    response="$(post_admin_json "/genres" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    genre_ids+=("$id")
  done
}

create_tags() {
  echo "Creating tags..."
  local i response id featured color payload
  for ((i = 1; i <= OTHER_ENTITY_COUNT; i++)); do
    featured="false"
    if (( i % 3 == 0 )); then
      featured="true"
    fi
    color="$(printf '#%02x%02x%02x' $(((i * 29) % 256)) $(((i * 53) % 256)) $(((i * 71) % 256)))"
    payload="$(printf '{"name":"Tag %d","description":"Sample tag %d","color":"%s","featured":%s}' \
      "$i" "$i" "$color" "$featured")"
    response="$(post_admin_json "/tags" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    tag_ids+=("$id")
  done
}

create_publishers() {
  echo "Creating publishers..."
  local i response id payload
  for ((i = 1; i <= OTHER_ENTITY_COUNT; i++)); do
    payload="$(printf '{"name":"Publisher %d","description":"Sample publisher %d","country":"Country %d","website":"https://publisher-%d.example.com"}' \
      "$i" "$i" "$i" "$i")"
    response="$(post_admin_json "/publishers" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    publisher_ids+=("$id")
  done
}

create_authors() {
  echo "Creating authors..."
  local i response id payload
  for ((i = 1; i <= OTHER_ENTITY_COUNT; i++)); do
    payload="$(printf '{"name":"Author %d","bio":"Biography for author %d","country":"Country %d","website":"https://author-%d.example.com"}' \
      "$i" "$i" "$i" "$i")"
    response="$(post_admin_json "/authors" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    author_ids+=("$id")
  done
}

create_clients() {
  echo "Creating clients..."
  local i response id payload
  for ((i = 1; i <= OTHER_ENTITY_COUNT; i++)); do
    payload="$(printf '{"name":"Reader %d","email":"reader_%d@example.com","password":"password_%d","city":"City %d","role":"CUSTOMER"}' \
      "$i" "$i" "$i" "$i")"
    response="$(post_admin_json "/clients" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    client_ids+=("$id")
  done
}

build_tag_id_json() {
  local first="$1"
  local second="$2"
  printf '[%s,%s]' "${tag_ids[$first]}" "${tag_ids[$second]}"
}

build_author_id_json() {
  local first="$1"
  local second="$2"
  printf '[%s,%s]' "${author_ids[$first]}" "${author_ids[$second]}"
}

build_publisher_id_json() {
  local first="$1"
  local second="$2"
  printf '[%s,%s]' "${publisher_ids[$first]}" "${publisher_ids[$second]}"
}

create_books() {
  echo "Creating books..."
  local i response id payload format featured popular new_arrival
  local genre_idx tag_a tag_b author_a author_b publisher_a publisher_b
  local price discount_price pages year publish_date

  for ((i = 1; i <= BOOK_COUNT; i++)); do
    genre_idx=$(((i - 1) % OTHER_ENTITY_COUNT))
    tag_a=$(((i - 1) % OTHER_ENTITY_COUNT))
    tag_b=$(((i + 2) % OTHER_ENTITY_COUNT))
    author_a=$(((i - 1) % OTHER_ENTITY_COUNT))
    author_b=$(((i + 3) % OTHER_ENTITY_COUNT))
    publisher_a=$(((i - 1) % OTHER_ENTITY_COUNT))
    publisher_b=$(((i + 1) % OTHER_ENTITY_COUNT))

    format="HARDCOVER"
    if (( i % 2 == 0 )); then
      format="PAPERBACK"
    fi

    featured="false"
    popular="false"
    new_arrival="false"
    if (( i % 10 == 0 )); then featured="true"; fi
    if (( i % 7 == 0 )); then popular="true"; fi
    if (( i > BOOK_COUNT - 15 )); then new_arrival="true"; fi

    price="$(printf '%d.%02d' $((12 + (i % 35))) $(((i * 17) % 100)))"
    discount_price="$(printf '%d.%02d' $((8 + (i % 25))) $(((i * 11) % 100)))"
    pages=$((180 + ((i * 19) % 520)))
    year=$((1998 + (i % 28)))
    publish_date="$(date -d "2000-01-01 +$(((i * 23) % 9000)) days" +%F)"

    payload="$(printf '{
      "title":"Sample Book %d",
      "authors":%s,
      "genreId":%s,
      "price":%s,
      "discountPrice":%s,
      "format":"%s",
      "pages":%d,
      "year":%d,
      "publishDate":"%s",
      "publisherIds":%s,
      "blurb":"Blurb for sample book %d",
      "description":"Short description for sample book %d",
      "longDescription":"Long description for sample book %d with more storefront text.",
      "tagIds":%s,
      "featured":%s,
      "popular":%s,
      "newArrival":%s
    }' \
      "$i" \
      "$(build_author_id_json "$author_a" "$author_b")" \
      "${genre_ids[$genre_idx]}" \
      "$price" \
      "$discount_price" \
      "$format" \
      "$pages" \
      "$year" \
      "$publish_date" \
      "$(build_publisher_id_json "$publisher_a" "$publisher_b")" \
      "$i" "$i" "$i" \
      "$(build_tag_id_json "$tag_a" "$tag_b")" \
      "$featured" "$popular" "$new_arrival")"

    response="$(post_admin_json "/books" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    book_ids+=("$id")
  done
}

build_order_items_json() {
  local index="$1"
  local book_a book_b book_c
  book_a="${book_ids[$(((index - 1) % BOOK_COUNT))]}"
  book_b="${book_ids[$(((index + 9) % BOOK_COUNT))]}"
  book_c="${book_ids[$(((index + 21) % BOOK_COUNT))]}"

  printf '[{"bookId":%s,"quantity":1},{"bookId":%s,"quantity":2},{"bookId":%s,"quantity":1}]' \
    "$book_a" "$book_b" "$book_c"
}

post_order_batch() {
  local start_index="$1"
  local end_index="$2"
  local json="["
  local i client_id status

  for ((i = start_index; i <= end_index; i++)); do
    client_id="${client_ids[$(((i - 1) % OTHER_ENTITY_COUNT))]}"
    status="PROCESSING"
    if (( i % 3 == 0 )); then
      status="DELIVERED"
    elif (( i % 3 == 1 )); then
      status="PACKED"
    fi

    json+="$(printf '{"clientId":%s,"status":"%s","paymentMethod":"CARD","deliveryCity":"City %d","items":%s}' \
      "$client_id" "$status" "$i" "$(build_order_items_json "$i")")"
    if (( i < end_index )); then
      json+=","
    fi
  done

  json+="]"
  curl "${CURL_OPTS[@]}" \
    --header "Content-Type: application/json" \
    --header "Authorization: Bearer ${ADMIN_TOKEN}" \
    --request POST \
    --data "$json" \
    "${BASE_URL}/orders/bulk" >/dev/null
}

post_single_order() {
  local index="$1"
  local client_id status payload
  client_id="${client_ids[$(((index - 1) % OTHER_ENTITY_COUNT))]}"
  status="PROCESSING"
  if (( index % 3 == 0 )); then
    status="DELIVERED"
  elif (( index % 3 == 1 )); then
    status="PACKED"
  fi

  payload="$(printf '{"clientId":%s,"status":"%s","paymentMethod":"CARD","deliveryCity":"City %d","items":%s}' \
    "$client_id" "$status" "$index" "$(build_order_items_json "$index")")"

  post_json "/orders" "$payload" --header "Authorization: Bearer ${ADMIN_TOKEN}" >/dev/null
}

create_orders() {
  echo "Creating orders..."
  local start=1 end
  while (( start <= ORDER_COUNT )); do
    end=$((start + ORDER_BATCH_SIZE - 1))
    if (( end > ORDER_COUNT )); then
      end=$ORDER_COUNT
    fi
    if ! post_order_batch "$start" "$end"; then
      echo "Bulk endpoint failed for orders ${start}-${end}, retrying one by one..."
      local i
      for ((i = start; i <= end; i++)); do
        post_single_order "$i"
      done
    fi
    start=$((end + 1))
  done
}

count_orders_from_api() {
  local response
  response="$(get_admin_json "/orders")"
  printf '%s' "$response" | grep -o '"orderCode"' | wc -l | tr -d ' '
}

print_summary() {
  local actual_orders
  actual_orders="$(count_orders_from_api)"
  if (( actual_orders < ORDER_COUNT )); then
    echo "Expected at least ${ORDER_COUNT} orders, but API returned ${actual_orders}." >&2
    exit 1
  fi

  printf 'Created via API:%s' $'\n'
  printf 'genres: %s%s' "${#genre_ids[@]}" $'\n'
  printf 'tags: %s%s' "${#tag_ids[@]}" $'\n'
  printf 'publishers: %s%s' "${#publisher_ids[@]}" $'\n'
  printf 'authors: %s%s' "${#author_ids[@]}" $'\n'
  printf 'clients: %s%s' "${#client_ids[@]}" $'\n'
  printf 'books: %s%s' "${#book_ids[@]}" $'\n'
  printf 'orders: %s%s' "${actual_orders}" $'\n'
}

require_command curl
require_command date

check_api
login_as_admin
create_genres
create_tags
create_publishers
create_authors
create_clients
create_books
create_orders
print_summary
