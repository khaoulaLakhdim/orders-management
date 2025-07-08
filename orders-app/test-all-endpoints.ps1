# Comprehensive Test Script for Orders App Backend
# This script tests all endpoints: auth, clients, orders

Write-Host "=== Orders App Backend Test Suite ===" -ForegroundColor Green
Write-Host ""

# Base URL
$baseUrl = "http://localhost:8080"

# Test data
$testClient = @{
    name = "Test Client"
    code = "TEST001"
    city = "Test City"
}

$testOrder = @{
    productName = "Test Product"
    quantity = 5
    price = 99.99
    orderDate = "2024-01-15"
}

# Function to make HTTP requests
function Invoke-TestRequest {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
        }
        
        if ($Body) {
            $params.Body = $Body
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-RestMethod @params
        return @{
            Success = $true
            StatusCode = $response.StatusCode
            Data = $response
        }
    }
    catch {
        return @{
            Success = $false
            StatusCode = $_.Exception.Response.StatusCode.value__
            Error = $_.Exception.Message
        }
    }
}

# Test 1: Health Check
Write-Host "1. Testing Health Check..." -ForegroundColor Yellow
$healthResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/ping"
if ($healthResult.Success) {
    Write-Host "✅ Health check passed: $($healthResult.Data)" -ForegroundColor Green
} else {
    Write-Host "❌ Health check failed: $($healthResult.Error)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Authentication
Write-Host "2. Testing Authentication..." -ForegroundColor Yellow

# Login
Write-Host "   - Testing login..." -ForegroundColor Cyan
$loginBody = @{
    username = "admin"
    password = "password"
} | ConvertTo-Json

$loginResult = Invoke-TestRequest -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
if ($loginResult.Success) {
    Write-Host "✅ Login successful" -ForegroundColor Green
    $token = $loginResult.Data.token
    $headers = @{ "Authorization" = "Bearer $token" }
} else {
    Write-Host "❌ Login failed: $($loginResult.Error)" -ForegroundColor Red
    $headers = @{}
}
Write-Host ""

# Test 3: Clients Endpoints
Write-Host "3. Testing Clients Endpoints..." -ForegroundColor Yellow

# Get all clients
Write-Host "   - Testing GET /api/clients..." -ForegroundColor Cyan
$clientsResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/clients" -Headers $headers
if ($clientsResult.Success) {
    Write-Host "✅ Get clients successful: $($clientsResult.Data.count) clients found" -ForegroundColor Green
} else {
    Write-Host "❌ Get clients failed: $($clientsResult.Error)" -ForegroundColor Red
}

# Create client
Write-Host "   - Testing POST /api/clients..." -ForegroundColor Cyan
$createClientBody = $testClient | ConvertTo-Json
$createClientResult = Invoke-TestRequest -Method "POST" -Url "$baseUrl/api/clients" -Body $createClientBody -Headers $headers
if ($createClientResult.Success) {
    Write-Host "✅ Create client successful" -ForegroundColor Green
    $clientId = $createClientResult.Data.client.id
} else {
    Write-Host "❌ Create client failed: $($createClientResult.Error)" -ForegroundColor Red
    $clientId = 1
}

# Get client by ID
Write-Host "   - Testing GET /api/clients/$clientId..." -ForegroundColor Cyan
$getClientResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/clients/$clientId" -Headers $headers
if ($getClientResult.Success) {
    Write-Host "✅ Get client by ID successful" -ForegroundColor Green
} else {
    Write-Host "❌ Get client by ID failed: $($getClientResult.Error)" -ForegroundColor Red
}

# Update client
Write-Host "   - Testing PUT /api/clients/$clientId..." -ForegroundColor Cyan
$updateClient = $testClient.Clone()
$updateClient.name = "Updated Test Client"
$updateClientBody = $updateClient | ConvertTo-Json
$updateClientResult = Invoke-TestRequest -Method "PUT" -Url "$baseUrl/api/clients/$clientId" -Body $updateClientBody -Headers $headers
if ($updateClientResult.Success) {
    Write-Host "✅ Update client successful" -ForegroundColor Green
} else {
    Write-Host "❌ Update client failed: $($updateClientResult.Error)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Orders Endpoints
Write-Host "4. Testing Orders Endpoints..." -ForegroundColor Yellow

# Get all orders (paginated)
Write-Host "   - Testing GET /api/orders..." -ForegroundColor Cyan
$ordersResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/orders?page=0&size=5" -Headers $headers
if ($ordersResult.Success) {
    Write-Host "✅ Get orders successful: $($ordersResult.Data.totalElements) total orders, $($ordersResult.Data.orders.Count) on this page" -ForegroundColor Green
} else {
    Write-Host "❌ Get orders failed: $($ordersResult.Error)" -ForegroundColor Red
}

# Create order
Write-Host "   - Testing POST /api/orders..." -ForegroundColor Cyan
$createOrder = $testOrder.Clone()
$createOrder.client = @{ id = $clientId }
$createOrderBody = $createOrder | ConvertTo-Json
$createOrderResult = Invoke-TestRequest -Method "POST" -Url "$baseUrl/api/orders" -Body $createOrderBody -Headers $headers
if ($createOrderResult.Success) {
    Write-Host "✅ Create order successful" -ForegroundColor Green
    $orderId = $createOrderResult.Data.order.id
} else {
    Write-Host "❌ Create order failed: $($createOrderResult.Error)" -ForegroundColor Red
    $orderId = 1
}

# Get order by ID
Write-Host "   - Testing GET /api/orders/$orderId..." -ForegroundColor Cyan
$getOrderResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/orders/$orderId" -Headers $headers
if ($getOrderResult.Success) {
    Write-Host "✅ Get order by ID successful" -ForegroundColor Green
} else {
    Write-Host "❌ Get order by ID failed: $($getOrderResult.Error)" -ForegroundColor Red
}

# Get orders filtered by client
Write-Host "   - Testing GET /api/orders?clientId=$clientId..." -ForegroundColor Cyan
$filteredOrdersResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/orders?clientId=$clientId&page=0&size=10" -Headers $headers
if ($filteredOrdersResult.Success) {
    Write-Host "✅ Get filtered orders successful: $($filteredOrdersResult.Data.totalElements) orders for client $clientId" -ForegroundColor Green
} else {
    Write-Host "❌ Get filtered orders failed: $($filteredOrdersResult.Error)" -ForegroundColor Red
}

# Update order
Write-Host "   - Testing PUT /api/orders/$orderId..." -ForegroundColor Cyan
$updateOrder = $testOrder.Clone()
$updateOrder.client = @{ id = $clientId }
$updateOrder.productName = "Updated Test Product"
$updateOrderBody = $updateOrder | ConvertTo-Json
$updateOrderResult = Invoke-TestRequest -Method "PUT" -Url "$baseUrl/api/orders/$orderId" -Body $updateOrderBody -Headers $headers
if ($updateOrderResult.Success) {
    Write-Host "✅ Update order successful" -ForegroundColor Green
} else {
    Write-Host "❌ Update order failed: $($updateOrderResult.Error)" -ForegroundColor Red
}
Write-Host ""

# Test 5: Error Handling
Write-Host "5. Testing Error Handling..." -ForegroundColor Yellow

# Test invalid client creation
Write-Host "   - Testing invalid client creation..." -ForegroundColor Cyan
$invalidClient = @{ name = "" } | ConvertTo-Json
$invalidClientResult = Invoke-TestRequest -Method "POST" -Url "$baseUrl/api/clients" -Body $invalidClient -Headers $headers
if (-not $invalidClientResult.Success -and $invalidClientResult.StatusCode -eq 400) {
    Write-Host "✅ Invalid client creation properly rejected" -ForegroundColor Green
} else {
    Write-Host "❌ Invalid client creation not properly handled" -ForegroundColor Red
}

# Test non-existent resource
Write-Host "   - Testing non-existent resource..." -ForegroundColor Cyan
$notFoundResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/clients/99999" -Headers $headers
if (-not $notFoundResult.Success -and $notFoundResult.StatusCode -eq 404) {
    Write-Host "✅ Non-existent resource properly returns 404" -ForegroundColor Green
} else {
    Write-Host "❌ Non-existent resource not properly handled" -ForegroundColor Red
}

# Test unauthorized access
Write-Host "   - Testing unauthorized access..." -ForegroundColor Cyan
$unauthorizedResult = Invoke-TestRequest -Method "GET" -Url "$baseUrl/api/clients"
if (-not $unauthorizedResult.Success -and $unauthorizedResult.StatusCode -eq 401) {
    Write-Host "✅ Unauthorized access properly rejected" -ForegroundColor Green
} else {
    Write-Host "❌ Unauthorized access not properly handled" -ForegroundColor Red
}
Write-Host ""

# Test 6: Cleanup (optional)
Write-Host "6. Testing Cleanup..." -ForegroundColor Yellow

# Delete order
Write-Host "   - Testing DELETE /api/orders/$orderId..." -ForegroundColor Cyan
$deleteOrderResult = Invoke-TestRequest -Method "DELETE" -Url "$baseUrl/api/orders/$orderId" -Headers $headers
if ($deleteOrderResult.Success) {
    Write-Host "✅ Delete order successful" -ForegroundColor Green
} else {
    Write-Host "❌ Delete order failed: $($deleteOrderResult.Error)" -ForegroundColor Red
}

# Delete client
Write-Host "   - Testing DELETE /api/clients/$clientId..." -ForegroundColor Cyan
$deleteClientResult = Invoke-TestRequest -Method "DELETE" -Url "$baseUrl/api/clients/$clientId" -Headers $headers
if ($deleteClientResult.Success) {
    Write-Host "✅ Delete client successful" -ForegroundColor Green
} else {
    Write-Host "❌ Delete client failed: $($deleteClientResult.Error)" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Suite Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend Integration Examples:" -ForegroundColor Cyan
Write-Host "// Login"
Write-Host "const loginResponse = await axios.post('http://localhost:8080/api/auth/login', {"
Write-Host "  username: 'admin',"
Write-Host "  password: 'password'"
Write-Host "});"
Write-Host "const token = loginResponse.data.token;"
Write-Host ""
Write-Host "// Get clients"
Write-Host "const clientsResponse = await axios.get('http://localhost:8080/api/clients', {"
Write-Host "  headers: { Authorization: `Bearer \${token}` }"
Write-Host "});"
Write-Host ""
Write-Host "// Get orders with pagination"
Write-Host "const ordersResponse = await axios.get('http://localhost:8080/api/orders?page=0&size=10', {"
Write-Host "  headers: { Authorization: `Bearer \${token}` }"
Write-Host "});" 