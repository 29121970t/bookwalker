#!/bin/bash

# ----------------------------------------------------------------------
#  Sample Data Populator for Bookwalker API
# ----------------------------------------------------------------------
# This script creates authors, publishers, books, clients, and orders.
# It assumes the API is running at http://localhost:8080 and that 'jq'
# is installed for JSON parsing.
# ----------------------------------------------------------------------

set -e  # Exit on any error

BASE_URL="http://localhost:8080"

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is not installed. Please install jq to run this script."
    exit 1
fi

echo "=== Starting population of sample data ==="

# ----------------------------------------------------------------------
# 1. Create Publishers
# ----------------------------------------------------------------------
echo "Creating publishers..."
publisher_ids=()

create_publisher() {
    local name="$1"
    response=$(curl -s -X POST "$BASE_URL/publishers" \
        -H "Content-Type: application/json" \
        -d "{\"name\": \"$name\"}")
    id=$(echo "$response" | jq -r '.id')
    if [ "$id" = "null" ] || [ -z "$id" ]; then
        echo "Failed to create publisher: $name"
        echo "Response: $response"
        exit 1
    fi
    publisher_ids+=("$id")
    echo "  Created publisher '$name' with ID $id"
}

create_publisher "Penguin Random House"
create_publisher "HarperCollins"
create_publisher "Simon & Schuster"
create_publisher "Hachette Book Group"
create_publisher "Macmillan Publishers"

echo ""

# ----------------------------------------------------------------------
# 2. Create Authors (at least 10)
# ----------------------------------------------------------------------
echo "Creating authors..."
author_ids=()

create_author() {
    local first="$1"
    local middle="$2"
    local last="$3"
    local bio="$4"
    response=$(curl -s -X POST "$BASE_URL/authors" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$first\",
            \"middleName\": \"$middle\",
            \"surname\": \"$last\",
            \"bio\": \"$bio\"
        }")
    id=$(echo "$response" | jq -r '.id')
    if [ "$id" = "null" ] || [ -z "$id" ]; then
        echo "Failed to create author: $first $last"
        echo "Response: $response"
        exit 1
    fi
    author_ids+=("$id")
    echo "  Created author '$first $last' with ID $id"
}

create_author "George" "R.R." "Martin" "American novelist, known for A Song of Ice and Fire."
create_author "J.K." "" "Rowling" "British author, creator of Harry Potter."
create_author "Dan" "" "Brown" "American author of thrillers like The Da Vinci Code."
create_author "Stephen" "Edwin" "King" "American author of horror, supernatural fiction, and fantasy."
create_author "Agatha" "" "Christie" "English writer known for her detective novels."
create_author "Isaac" "" "Asimov" "American writer and professor of biochemistry, famous for science fiction."
create_author "Jane" "" "Austen" "English novelist known for Pride and Prejudice."
create_author "Ernest" "Miller" "Hemingway" "American journalist and novelist, Nobel Prize winner."
create_author "Gabriel" "José" "García Márquez" "Colombian novelist, Nobel Prize winner, pioneer of magical realism."
create_author "Toni" "" "Morrison" "American novelist, editor, and professor, Nobel Prize winner."
create_author "Haruki" "" "Murakami" "Japanese writer, known for Kafka on the Shore and Norwegian Wood."
create_author "Philip" "Kindred" "Dick" "American science fiction writer, influenced Blade Runner."

echo ""

# ----------------------------------------------------------------------
# 3. Create Books (at least 10)
# ----------------------------------------------------------------------
echo "Creating books..."
book_ids=()

create_book() {
    local name="$1"
    local authors_ref="$2"      # JSON array of author IDs
    local page_count="$3"
    local publish_date="$4"
    local publisher_id="$5"
    local price="$6"

    response=$(curl -s -X POST "$BASE_URL/books" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$name\",
            \"authors\": $authors_ref,
            \"pageCount\": $page_count,
            \"publishDate\": \"$publish_date\",
            \"publisher\": $publisher_id,
            \"price\": $price
        }")
    id=$(echo "$response" | jq -r '.id')
    if [ "$id" = "null" ] || [ -z "$id" ]; then
        echo "Failed to create book: $name"
        echo "Response: $response"
        exit 1
    fi
    book_ids+=("$id")
    echo "  Created book '$name' with ID $id"
}

# Helper to pick random publisher ID from the list
random_publisher() {
    local idx=$(( RANDOM % ${#publisher_ids[@]} ))
    echo "${publisher_ids[$idx]}"
}

# Helper to create a JSON array with one random author (or two)
random_author_array() {
    local count=${1:-1}   # number of authors, default 1
    local idx
    local -a selected=()
    for ((i=0; i<count; i++)); do
        idx=$(( RANDOM % ${#author_ids[@]} ))
        selected+=("${author_ids[$idx]}")
    done
    # Remove duplicates by sorting unique (simple approach)
    # For simplicity we just output the array as is, but duplicates are fine.
    printf '%s\n' "${selected[@]}" | jq -R . | jq -s -c .
}

# Create books
# We'll use specific titles for clarity
create_book "A Game of Thrones" "$(random_author_array 1)" 694 "1996-08-06" "$(random_publisher)" 9.99
create_book "Harry Potter and the Philosopher's Stone" "$(random_author_array 1)" 223 "1997-06-26" "$(random_publisher)" 12.99
create_book "The Da Vinci Code" "$(random_author_array 1)" 489 "2003-03-18" "$(random_publisher)" 14.95
create_book "The Shining" "$(random_author_array 1)" 447 "1977-01-28" "$(random_publisher)" 11.50
create_book "Murder on the Orient Express" "$(random_author_array 1)" 256 "1934-01-01" "$(random_publisher)" 8.99
create_book "Foundation" "$(random_author_array 1)" 255 "1951-06-01" "$(random_publisher)" 10.99
create_book "Pride and Prejudice" "$(random_author_array 1)" 279 "1813-01-28" "$(random_publisher)" 7.99
create_book "The Old Man and the Sea" "$(random_author_array 1)" 127 "1952-09-01" "$(random_publisher)" 6.99
create_book "One Hundred Years of Solitude" "$(random_author_array 1)" 417 "1967-06-05" "$(random_publisher)" 13.50
create_book "Beloved" "$(random_author_array 1)" 324 "1987-09-02" "$(random_publisher)" 12.00
create_book "Kafka on the Shore" "$(random_author_array 1)" 505 "2002-09-12" "$(random_publisher)" 15.00
create_book "Do Androids Dream of Electric Sheep?" "$(random_author_array 1)" 210 "1968-01-01" "$(random_publisher)" 9.50

echo ""

# ----------------------------------------------------------------------
# 4. Create Clients
# ----------------------------------------------------------------------
echo "Creating clients..."
client_ids=()

create_client() {
    local username="$1"
    local password="$2"
    response=$(curl -s -X POST "$BASE_URL/clients" \
        -H "Content-Type: application/json" \
        -d "{
            \"userName\": \"$username\",
            \"password\": \"$password\"
        }")
    id=$(echo "$response" | jq -r '.id')
    if [ "$id" = "null" ] || [ -z "$id" ]; then
        echo "Failed to create client: $username"
        echo "Response: $response"
        exit 1
    fi
    client_ids+=("$id")
    echo "  Created client '$username' with ID $id"
}

create_client "alice_wonder" "alice123"
create_client "bob_reader" "bobPass!"
create_client "charlie_bookworm" "charlie456"

echo ""

# ----------------------------------------------------------------------
# 5. Create Orders
# ----------------------------------------------------------------------
echo "Creating orders..."

create_order() {
    local client_id="$1"
    local books_array="$2"   # JSON array of book IDs
    response=$(curl -s -X POST "$BASE_URL/orders" \
        -H "Content-Type: application/json" \
        -d "{
            \"client\": $client_id,
            \"books\": $books_array
        }")
    id=$(echo "$response" | jq -r '.id')
    if [ "$id" = "null" ] || [ -z "$id" ]; then
        echo "Failed to create order for client $client_id"
        echo "Response: $response"
        exit 1
    fi
    echo "  Created order with ID $id"
}

# Helper to pick a random subset of book IDs as JSON array
random_books_array() {
    local count=${1:-2}   # number of books to pick, default 2
    local idx
    local -a selected=()
    for ((i=0; i<count; i++)); do
        idx=$(( RANDOM % ${#book_ids[@]} ))
        selected+=("${book_ids[$idx]}")
    done
    printf '%s\n' "${selected[@]}" | jq -R . | jq -s -c .
}

# Create a few orders
create_order "${client_ids[0]}" "$(random_books_array 2)"   # Alice buys 2 random books
create_order "${client_ids[0]}" "$(random_books_array 1)"   # Alice buys another 1
create_order "${client_ids[1]}" "$(random_books_array 3)"   # Bob buys 3 random books
create_order "${client_ids[2]}" "$(random_books_array 2)"   # Charlie buys 2 random books

echo ""
echo "=== Data population completed successfully ==="
echo "Summary:"
echo "  Publishers: ${#publisher_ids[@]}"
echo "  Authors:    ${#author_ids[@]}"
echo "  Books:      ${#book_ids[@]}"
echo "  Clients:    ${#client_ids[@]}"
