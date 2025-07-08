# Authentication Test Script for Orders App
# Run this script to test all authentication endpoints

Write-Host "🔐 Testing Orders App Authentication Endpoints" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green

# Test 1: Login with admin
Write-Host "`n1. Testing Login with admin..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "password"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "✅ Login successful!" -ForegroundColor Green
    Write-Host "User: $($loginResponse.user.username)" -ForegroundColor Cyan
    Write-Host "Role: $($loginResponse.user.role)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Get current user
Write-Host "`n2. Testing Get Current User..." -ForegroundColor Yellow
try {
    $meResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET
    Write-Host "✅ Current user retrieved!" -ForegroundColor Green
    Write-Host "User: $($meResponse.user.username)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Get current user failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Login with different users
Write-Host "`n3. Testing Login with different users..." -ForegroundColor Yellow
$users = @("manager", "user1", "user2")

foreach ($user in $users) {
    $userLoginBody = @{
        username = $user
        password = "password"
    } | ConvertTo-Json
    
    try {
        $userResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $userLoginBody
        Write-Host "✅ $user login successful (Role: $($userResponse.user.role))" -ForegroundColor Green
    } catch {
        Write-Host "❌ $user login failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 4: Test invalid login
Write-Host "`n4. Testing Invalid Login..." -ForegroundColor Yellow
$invalidBody = @{
    username = "invaliduser"
    password = "wrongpassword"
} | ConvertTo-Json

try {
    $invalidResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $invalidBody
    Write-Host "❌ Invalid login should have failed!" -ForegroundColor Red
} catch {
    Write-Host "✅ Invalid login correctly rejected" -ForegroundColor Green
}

# Test 5: Register new user
Write-Host "`n5. Testing User Registration..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser"
    password = "testpass123"
    role = "USER"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $registerBody
    Write-Host "✅ User registration successful!" -ForegroundColor Green
    Write-Host "New user: $($registerResponse.user.username)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ User registration failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Test logout
Write-Host "`n6. Testing Logout..." -ForegroundColor Yellow
try {
    $logoutResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/logout" -Method POST
    Write-Host "✅ Logout successful!" -ForegroundColor Green
} catch {
    Write-Host "❌ Logout failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Authentication testing completed!" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green 