#!/bin/bash

echo "Testing Patient Registration and Login API"
echo "=========================================="

# Test patient registration
echo "1. Testing Patient Registration..."
curl --location 'http://localhost:8765/api/v1/patient/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@email.com",
  "phoneNumber": "+1234567890",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "dateOfBirth": "1990-05-15",
  "gender": "FEMALE",
  "address": {
    "street": "456 Main Street",
    "city": "Boston",
    "state": "MA",
    "zip": "02101"
  },
  "emergencyContact": {
    "name": "John Smith",
    "phone": "+1234567891",
    "relationship": "spouse"
  },
  "insuranceInfo": {
    "provider": "Blue Cross",
    "policyNumber": "BC123456789"
  },
  "medicalHistory": [
    "Diabetes",
    "Hypertension"
  ]
}' | jq '.'

echo -e "\n2. Testing Patient Login..."
curl --location 'http://localhost:8765/api/v1/patient/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "jane.smith@email.com",
  "password": "SecurePassword123!"
}' | jq '.'

echo -e "\nTest completed!" 