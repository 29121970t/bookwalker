#!/bin/bash

# ----------------------------------------------------------------------
#  Expanded Sample Data Populator for Bookwalker API (200 entries per entity)
# ----------------------------------------------------------------------

set -e

BASE_URL="http://localhost:8080"

if ! command -v jq &> /dev/null; then
    echo "Error: jq is not installed."
    exit 1
fi

echo "=== Starting population of 200 records per entity ==="

# ----------------------------------------------------------------------
# 1. Создание Издателей (200 записей)
# ----------------------------------------------------------------------
echo "Creating publishers..."
publisher_ids=()
for i in {1..200}; do
    name="Publisher $i"
    response=$(curl -s -X POST "$BASE_URL/publishers" -H "Content-Type: application/json" -d "{\"name\": \"$name\"}")
    id=$(echo "$response" | jq -r '.id')
    publisher_ids+=("$id")
    if (( i % 20 == 0 )); then
        echo "  Created $i publishers (latest ID: $id)"
    fi
done
echo "  Total publishers created: ${#publisher_ids[@]}"

# ----------------------------------------------------------------------
# 2. Создание Авторов (200 записей)
# ----------------------------------------------------------------------
echo -e "\nCreating authors..."
# Списки имён и фамилий для комбинаций
first_names=(
    "James" "Mary" "John" "Patricia" "Robert" "Jennifer" "Michael" "Linda" "William" "Elizabeth"
    "David" "Barbara" "Richard" "Susan" "Joseph" "Jessica" "Thomas" "Sarah" "Charles" "Karen"
)
surnames=(
    "Smith" "Johnson" "Williams" "Brown" "Jones" "Garcia" "Miller" "Davis" "Rodriguez" "Martinez"
    "Hernandez" "Lopez" "Gonzalez" "Wilson" "Anderson" "Thomas" "Taylor" "Moore" "Jackson" "Martin"
)

author_ids=()
for i in {1..200}; do
    first_idx=$(( (i-1) % ${#first_names[@]} ))
    last_idx=$(( (i-1) % ${#surnames[@]} ))
    first="${first_names[$first_idx]}"
    surname="${surnames[$last_idx]}"
    bio="Author bio for $first $surname (ID $i)"

    response=$(curl -s -X POST "$BASE_URL/authors" \
        -H "Content-Type: application/json" \
        -d "{\"name\": \"$first\", \"middleName\": \"\", \"surname\": \"$surname\", \"bio\": \"$bio\"}")
    id=$(echo "$response" | jq -r '.id')
    author_ids+=("$id")
    if (( i % 20 == 0 )); then
        echo "  Created $i authors (latest: $first $surname, ID: $id)"
    fi
done
echo "  Total authors created: ${#author_ids[@]}"

# ----------------------------------------------------------------------
# 3. Создание Книг (200 записей)
# ----------------------------------------------------------------------
echo -e "\nCreating books..."
book_ids=()
for i in {1..200}; do
    # Случайный издатель
    p_id=${publisher_ids[$(( RANDOM % ${#publisher_ids[@]} ))]}

    # Количество авторов: 1 (70%) или 2-3 (30%)
    if [ $(( RANDOM % 10 )) -lt 7 ]; then
        auth_count=1
    else
        auth_count=$(( RANDOM % 2 + 2 ))
    fi

    # Выбор уникальных авторов
    random_auth_json=$(printf "%s\n" "${author_ids[@]}" | shuf -n "$auth_count" | jq -R . | jq -s -c .)

    # Случайные характеристики книги
    title="Book Title $i"
    page_count=$(( RANDOM % 900 + 100 ))           # от 100 до 999
    year=$(( RANDOM % 26 + 2000 ))                 # от 2000 до 2025
    publish_date="$year-01-01"
    price=$(printf "%.2f" "$(echo "scale=2; $RANDOM/1000 + 5" | bc)")

    response=$(curl -s -X POST "$BASE_URL/books" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$title\",
            \"authors\": $random_auth_json,
            \"pageCount\": $page_count,
            \"publishDate\": \"$publish_date\",
            \"publisher\": $p_id,
            \"price\": $price
        }")

    id=$(echo "$response" | jq -r '.id')
    if [ "$id" != "null" ] && [ -n "$id" ]; then
        book_ids+=("$id")
        if (( i % 20 == 0 )); then
            echo "  Created $i books (latest: '$title' with $auth_count author(s), ID: $id)"
        fi
    else
        echo "  Failed to create book: $title. Response: $response"
    fi
done
echo "  Total books created: ${#book_ids[@]}"

# ----------------------------------------------------------------------
# 4. Создание Клиентов (200 записей)
# ----------------------------------------------------------------------
echo -e "\nCreating clients..."
client_ids=()
for i in {1..200}; do
    username="user_$i"
    password="pass$i"
    response=$(curl -s -X POST "$BASE_URL/clients" \
        -H "Content-Type: application/json" \
        -d "{\"userName\": \"$username\", \"password\": \"$password\"}")
    id=$(echo "$response" | jq -r '.id')
    client_ids+=("$id")
    if (( i % 20 == 0 )); then
        echo "  Created $i clients (latest: $username, ID: $id)"
    fi
done
echo "  Total clients created: ${#client_ids[@]}"

# ----------------------------------------------------------------------
# 5. Создание Заказов (200 записей)
# ----------------------------------------------------------------------
echo -e "\nCreating orders..."
for i in {1..200}; do
    # Случайный клиент
    c_id=${client_ids[$(( RANDOM % ${#client_ids[@]} ))]}
    # Случайное количество книг от 1 до 3
    b_count=$(( RANDOM % 3 + 1 ))
    b_array=$(printf '%s\n' "${book_ids[@]}" | shuf -n "$b_count" | jq -R . | jq -s -c .)

    curl -s -X POST "$BASE_URL/orders" \
        -H "Content-Type: application/json" \
        -d "{\"client\": $c_id, \"books\": $b_array}" > /dev/null

    if (( i % 20 == 0 )); then
        echo "  Created $i orders (latest for client ID $c_id with $b_count books)"
    fi
done
echo "  Total orders created: 200"

echo -e "\n=== Data population completed successfully ==="