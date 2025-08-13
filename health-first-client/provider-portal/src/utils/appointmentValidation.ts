import * as yup from 'yup';

export const appointmentSchema = yup.object({
  patientId: yup.string().required('Patient is required'),
  providerId: yup.string().required('Provider is required'),
  appointmentType: yup.string().required('Appointment type is required'),
  location: yup.string().oneOf(['In-Clinic', 'Telehealth']).required('Location is required'),
  date: yup.string().required('Date is required'),
  startTime: yup.string().required('Start time is required'),
  endTime: yup.string().required('End time is required'),
  notes: yup.string(),
  status: yup.string().oneOf(['Scheduled', 'Confirmed', 'Cancelled']).required('Status is required'),
  isRecurring: yup.boolean(),
  recurrenceRule: yup.string().when('isRecurring', {
    is: true,
    then: (schema) => schema.required('Recurrence rule is required when recurring is enabled'),
    otherwise: (schema) => schema.optional(),
  }),
}).test('valid-time-range', 'End time must be after start time', (value) => {
  if (!value.startTime || !value.endTime) return true;
  
  const start = new Date(`2000-01-01T${value.startTime}`);
  const end = new Date(`2000-01-01T${value.endTime}`);
  
  return end > start;
}).test('valid-date', 'Appointment date cannot be in the past', (value) => {
  if (!value.date) return true;
  
  const appointmentDate = new Date(value.date);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  return appointmentDate >= today;
});

export const appointmentTypes = [
  { value: 'initial', label: 'Initial Consultation' },
  { value: 'follow-up', label: 'Follow-up' },
  { value: 'consultation', label: 'Consultation' },
  { value: 'lab', label: 'Lab Work' },
  { value: 'procedure', label: 'Procedure' },
  { value: 'emergency', label: 'Emergency' },
  { value: 'routine', label: 'Routine Checkup' },
];

export const locations = [
  { value: 'In-Clinic', label: 'In-Clinic' },
  { value: 'Telehealth', label: 'Telehealth' },
];

export const appointmentStatuses = [
  { value: 'Scheduled', label: 'Scheduled' },
  { value: 'Confirmed', label: 'Confirmed' },
  { value: 'Cancelled', label: 'Cancelled' },
];

export const recurrenceFrequencies = [
  { value: 'daily', label: 'Daily' },
  { value: 'weekly', label: 'Weekly' },
  { value: 'monthly', label: 'Monthly' },
  { value: 'yearly', label: 'Yearly' },
];

export const recurrenceDurations = [
  { value: '2', label: '2 times' },
  { value: '3', label: '3 times' },
  { value: '4', label: '4 times' },
  { value: '5', label: '5 times' },
  { value: '6', label: '6 times' },
  { value: '8', label: '8 times' },
  { value: '10', label: '10 times' },
  { value: '12', label: '12 times' },
]; 