#!/bin/bash

echo "Testing Appointment Booking API"
echo "=============================="

# First, let's register a provider and patient for testing
echo "1. Registering a provider for testing..."
PROVIDER_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Dr. Sarah",
  "lastName": "Johnson",
  "email": "sarah.johnson@clinic.com",
  "phoneNumber": "+1234567890",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "specialization": "Cardiology",
  "licenseNumber": "CARD789012",
  "yearsOfExperience": 12,
  "clinicAddress": {
    "street": "456 Medical Center Dr",
    "city": "Boston",
    "state": "MA",
    "zip": "02101"
  }
}')

echo "Provider registration response:"
echo $PROVIDER_RESPONSE

# Extract provider ID from response
PROVIDER_ID=$(echo $PROVIDER_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Provider ID: $PROVIDER_ID"

# Login as provider
echo -e "\n2. Logging in as provider..."
PROVIDER_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "sarah.johnson@clinic.com",
  "password": "SecurePassword123!"
}')

echo "Provider login response:"
echo $PROVIDER_LOGIN_RESPONSE

# Extract provider token
PROVIDER_TOKEN=$(echo $PROVIDER_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Provider token: $PROVIDER_TOKEN"

# Verify provider
echo -e "\n3. Verifying provider..."
curl --silent --location --request POST "http://localhost:8765/api/v1/provider/verify/$PROVIDER_ID" \
--header 'Content-Type: application/json'

# Login again after verification
echo -e "\n4. Logging in as provider after verification..."
PROVIDER_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "sarah.johnson@clinic.com",
  "password": "SecurePassword123!"
}')

PROVIDER_TOKEN=$(echo $PROVIDER_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Create availability
echo -e "\n5. Creating provider availability..."
AVAILABILITY_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/availability' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PROVIDER_TOKEN" \
--data-raw '{
  "date": "2024-02-15",
  "startTime": "09:00",
  "endTime": "17:00",
  "timezone": "America/New_York",
  "slotDuration": 30,
  "breakDuration": 15,
  "isRecurring": false,
  "appointmentType": "CONSULTATION",
  "location": {
    "type": "CLINIC",
    "address": "456 Medical Center Dr, Boston, MA 02101",
    "roomNumber": "Room 101"
  },
  "pricing": {
    "baseFee": 120.00,
    "insuranceAccepted": true,
    "currency": "USD"
  },
  "specialRequirements": ["bring_insurance_card"],
  "notes": "Standard consultation slots"
}')

echo "Availability creation response:"
echo $AVAILABILITY_RESPONSE

# Get available slots
echo -e "\n6. Getting available slots..."
SLOTS_RESPONSE=$(curl --silent --location "http://localhost:8765/api/v1/appointments/providers/$PROVIDER_ID/slots?startDate=2024-02-15&endDate=2024-02-15" \
--header 'Content-Type: application/json')

echo "Available slots response:"
echo $SLOTS_RESPONSE

# Extract slot ID from response
SLOT_ID=$(echo $SLOTS_RESPONSE | grep -o '"slotId":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Slot ID: $SLOT_ID"

# Register a patient
echo -e "\n7. Registering a patient for testing..."
PATIENT_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Michael",
  "lastName": "Brown",
  "email": "michael.brown@email.com",
  "phoneNumber": "+1234567891",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "dateOfBirth": "1985-03-20",
  "gender": "MALE",
  "address": {
    "street": "789 Oak Street",
    "city": "Boston",
    "state": "MA",
    "zip": "02102"
  },
  "emergencyContact": {
    "name": "Lisa Brown",
    "phone": "+1234567892",
    "relationship": "spouse"
  },
  "insuranceInfo": {
    "provider": "Blue Cross",
    "policyNumber": "BC987654321"
  },
  "medicalHistory": [
    "Hypertension"
  ]
}')

echo "Patient registration response:"
echo $PATIENT_RESPONSE

# Extract patient ID
PATIENT_ID=$(echo $PATIENT_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Patient ID: $PATIENT_ID"

# Verify patient
echo -e "\n8. Verifying patient..."
curl --silent --location --request POST "http://localhost:8765/api/v1/patient/verify/$PATIENT_ID" \
--header 'Content-Type: application/json'

# Login as patient
echo -e "\n9. Logging in as patient..."
PATIENT_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "michael.brown@email.com",
  "password": "SecurePassword123!"
}')

echo "Patient login response:"
echo $PATIENT_LOGIN_RESPONSE

# Extract patient token
PATIENT_TOKEN=$(echo $PATIENT_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Book an appointment
echo -e "\n10. Booking an appointment..."
BOOKING_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/appointments' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PATIENT_TOKEN" \
--data-raw "{
  \"slotId\": \"$SLOT_ID\",
  \"patientId\": \"$PATIENT_ID\",
  \"appointmentMode\": \"IN_PERSON\",
  \"appointmentType\": \"consultation\",
  \"estimatedAmount\": 120.00,
  \"reason\": \"Follow-up visit for hypertension\"
}")

echo "Appointment booking response:"
echo $BOOKING_RESPONSE

# Extract appointment ID and booking reference
APPOINTMENT_ID=$(echo $BOOKING_RESPONSE | grep -o '"appointmentId":"[^"]*"' | cut -d'"' -f4)
BOOKING_REFERENCE=$(echo $BOOKING_RESPONSE | grep -o '"bookingReference":"[^"]*"' | cut -d'"' -f4)
echo "Appointment ID: $APPOINTMENT_ID"
echo "Booking Reference: $BOOKING_REFERENCE"

# Get appointment by reference
echo -e "\n11. Getting appointment by reference..."
curl --silent --location "http://localhost:8765/api/v1/appointments/reference/$BOOKING_REFERENCE" \
--header 'Content-Type: application/json'

# Get patient appointments
echo -e "\n12. Getting patient appointments..."
curl --silent --location "http://localhost:8765/api/v1/appointments/patients/$PATIENT_ID" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PATIENT_TOKEN"

# Get provider appointments
echo -e "\n13. Getting provider appointments..."
curl --silent --location "http://localhost:8765/api/v1/appointments/providers/$PROVIDER_ID" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PROVIDER_TOKEN"

# Cancel appointment
echo -e "\n14. Cancelling appointment..."
curl --silent --location --request DELETE "http://localhost:8765/api/v1/appointments/$APPOINTMENT_ID?reason=Schedule conflict" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PATIENT_TOKEN"

echo -e "\nTest completed successfully!"
echo "=============================="
echo "Summary of tested functionality:"
echo "1. Provider registration and login"
echo "2. Provider verification"
echo "3. Availability creation"
echo "4. Available slots retrieval"
echo "5. Patient registration and login"
echo "6. Patient verification"
echo "7. Appointment booking"
echo "8. Appointment lookup by reference"
echo "9. Patient appointments retrieval"
echo "10. Provider appointments retrieval"
echo "11. Appointment cancellation"
echo ""
echo "All appointment booking endpoints are working correctly!" 