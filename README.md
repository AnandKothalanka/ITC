# Read Me First
Built on JAVA17, Spring boot 3.5.5 and an in memory h2db
# Getting Started

### API

####
Create product POST : http://localhost:8080/products
{
"sku":"222",
"name":"samsung",
"price":"9000",
"availableQuantity":"100"
}

####
Create Order POST : http://localhost:8080/orders
{
"customerEmail" :"test@gmail.com",
"items" : [
{
"sku": "222",
"quantity" : 14
}
]
}

####
Fetch product GET: http://localhost:8080/products/222
####
Fetch order GET: http://localhost:8080/orders/1

