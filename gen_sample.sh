#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
RECORDS_PER_ENTITY="${RECORDS_PER_ENTITY:-220}"
ORDER_BATCH_SIZE="${ORDER_BATCH_SIZE:-20}"
ORDER_BASE_EPOCH="$(date -d '2024-01-01 10:00:00' +%s)"
CURL_OPTS=(
  --silent
  --show-error
  --fail-with-body
  --header "Content-Type: application/json"
)

declare -a publisher_ids=()
declare -a author_ids=()
declare -a client_ids=()
declare -a book_ids=()

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

extract_id() {
  sed -n 's/.*"id"[[:space:]]*:[[:space:]]*\([0-9][0-9]*\).*/\1/p' | head -n 1
}

post_json() {
  local endpoint="$1"
  local payload="$2"
  curl "${CURL_OPTS[@]}" \
    --request POST \
    --data "$payload" \
    "${BASE_URL}${endpoint}"
}

get_json() {
  local endpoint="$1"
  curl --silent --show-error --fail "${BASE_URL}${endpoint}"
}

format_order_date() {
  local index="$1"
  local ts
  ts=$((ORDER_BASE_EPOCH + ((index - 1) * 6 * 3600)))
  date -d "@${ts}" '+%Y-%m-%dT%H:%M:%S'
}

check_api() {
  echo "Checking API availability at ${BASE_URL}..."
  curl --silent --show-error --fail "${BASE_URL}/api-docs" >/dev/null
}

create_publishers() {
  echo "Creating publishers..."
  local i response id
  for ((i = 1; i <= RECORDS_PER_ENTITY; i++)); do
    response="$(post_json "/publishers" "{\"name\":\"Publisher ${i}\"}")"
    id="$(printf '%s' "$response" | extract_id)"
    publisher_ids+=("$id")
  done
}

create_authors() {
  echo "Creating authors..."
  local i response id payload
  for ((i = 1; i <= RECORDS_PER_ENTITY; i++)); do
    payload="$(printf '{"middleName":"Middle%s","name":"AuthorName%s","surname":"Surname%s","bio":"Biography for author %s"}' "$i" "$i" "$i" "$i")"
    response="$(post_json "/authors" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    author_ids+=("$id")
  done
}

create_clients() {
  echo "Creating clients..."
  local i response id payload
  for ((i = 1; i <= RECORDS_PER_ENTITY; i++)); do
    payload="$(printf '{"username":"reader_%s","password":"password_%s"}' "$i" "$i")"
    response="$(post_json "/clients" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    client_ids+=("$id")
  done
}

create_books() {
  echo "Creating books..."
  local i response id payload publish_date page_count price publisher_id
  local author_a author_b author_c
  for ((i = 1; i <= RECORDS_PER_ENTITY; i++)); do
    publisher_id="${publisher_ids[$(((i - 1) % RECORDS_PER_ENTITY))]}"
    author_a="${author_ids[$(((i - 1) % RECORDS_PER_ENTITY))]}"
    author_b="${author_ids[$(((i + 6) % RECORDS_PER_ENTITY))]}"
    author_c="${author_ids[$(((i + 18) % RECORDS_PER_ENTITY))]}"
    publish_date="$(date -d "2000-01-01 +$(((i * 43) % 9000)) days" +%F)"
    page_count=$((120 + ((i * 37) % 780)))
    price="$(printf '%d.%02d' $((5 + ((i * 17) % 95))) $(((i * 13) % 100)))"
    payload="$(printf '{"authors":[%s,%s,%s],"name":"Book Title %s","publishDate":"%s","pageCount":%s,"publisher":%s,"price":%s}' \
      "$author_a" "$author_b" "$author_c" "$i" "$publish_date" "$page_count" "$publisher_id" "$price")"
    response="$(post_json "/books" "$payload")"
    id="$(printf '%s' "$response" | extract_id)"
    book_ids+=("$id")
  done
}

post_order_batch() {
  local start_index="$1"
  local end_index="$2"
  local json="["
  local i client_id book_a book_b book_c book_d order_date

  for ((i = start_index; i <= end_index; i++)); do
    client_id="${client_ids[$(((i - 1) % RECORDS_PER_ENTITY))]}"
    book_a="${book_ids[$(((i - 1) % RECORDS_PER_ENTITY))]}"
    book_b="${book_ids[$(((i + 10) % RECORDS_PER_ENTITY))]}"
    book_c="${book_ids[$(((i + 22) % RECORDS_PER_ENTITY))]}"
    book_d="${book_ids[$(((i + 46) % RECORDS_PER_ENTITY))]}"
    order_date="$(format_order_date "$i")"
    json+="$(printf '{"client":%s,"books":[%s,%s,%s,%s],"date":"%s"}' \
      "$client_id" "$book_a" "$book_b" "$book_c" "$book_d" "$order_date")"
    if (( i < end_index )); then
      json+=","
    fi
  done

  json+="]"
  post_json "/orders/bulk" "$json" >/dev/null
}

post_single_order() {
  local index="$1"
  local client_id book_a book_b book_c book_d order_date payload

  client_id="${client_ids[$(((index - 1) % RECORDS_PER_ENTITY))]}"
  book_a="${book_ids[$(((index - 1) % RECORDS_PER_ENTITY))]}"
  book_b="${book_ids[$(((index + 10) % RECORDS_PER_ENTITY))]}"
  book_c="${book_ids[$(((index + 22) % RECORDS_PER_ENTITY))]}"
  book_d="${book_ids[$(((index + 46) % RECORDS_PER_ENTITY))]}"
  order_date="$(format_order_date "$index")"
  payload="$(printf '{"client":%s,"books":[%s,%s,%s,%s],"date":"%s"}' \
    "$client_id" "$book_a" "$book_b" "$book_c" "$book_d" "$order_date")"
  post_json "/orders" "$payload" >/dev/null
}

create_orders() {
  echo "Creating orders via bulk API..."
  local start=1
  local end
  while (( start <= RECORDS_PER_ENTITY )); do
    end=$((start + ORDER_BATCH_SIZE - 1))
    if (( end > RECORDS_PER_ENTITY )); then
      end=$RECORDS_PER_ENTITY
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
  response="$(get_json "/orders")"
  printf '%s' "$response" | grep -o '"date"' | wc -l | tr -d ' '
}

print_summary() {
  local actual_orders
  actual_orders="$(count_orders_from_api)"
  if (( actual_orders < RECORDS_PER_ENTITY )); then
    echo "Expected at least ${RECORDS_PER_ENTITY} orders, but API returned ${actual_orders}." >&2
    exit 1
  fi

  printf 'Created via API:%s' $'\n'
  printf 'publishers: %s%s' "${#publisher_ids[@]}" $'\n'
  printf 'authors: %s%s' "${#author_ids[@]}" $'\n'
  printf 'clients: %s%s' "${#client_ids[@]}" $'\n'
  printf 'books: %s%s' "${#book_ids[@]}" $'\n'
  printf 'orders: %s%s' "${actual_orders}" $'\n'
}

require_command curl
require_command date

check_api
create_publishers
create_authors
create_clients
create_books
create_orders
print_summary
