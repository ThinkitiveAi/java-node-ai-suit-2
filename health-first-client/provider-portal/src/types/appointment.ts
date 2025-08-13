export interface Patient {
  id: string;
  name: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
}

export interface Provider {
  id: string;
  name: string;
  specialization?: string;
  email: string;
}

export interface Appointment {
  id: string;
  patientId: string;
  providerId: string;
  appointmentType: string;
  location: 'In-Clinic' | 'Telehealth';
  date: string; // YYYY-MM-DD
  startTime: string; // HH:mm
  endTime: string;   // HH:mm
  notes?: string;
  status: 'Scheduled' | 'Confirmed' | 'Cancelled';
  isRecurring: boolean;
  recurrenceRule?: string; // e.g., "RRULE:FREQ=WEEKLY;COUNT=4"
  createdAt?: string;
  updatedAt?: string;
}

export interface AppointmentFormData {
  patientId: string;
  providerId: string;
  appointmentType: string;
  location: 'In-Clinic' | 'Telehealth';
  date: string;
  startTime: string;
  endTime: string;
  notes?: string;
  status: 'Scheduled' | 'Confirmed' | 'Cancelled';
  isRecurring: boolean;
  recurrenceRule?: string;
}

export interface BookAppointmentModalProps {
  open: boolean;
  onClose: () => void;
  onSave: (appointment: Appointment) => void;
  existingAppointment?: Appointment;
  patients?: Patient[];
  providers?: Provider[];
}

// For the View Appointment List screen
export interface AppointmentListItem {
  id: string;
  patientName: string;
  date: string; // YYYY-MM-DD
  time: string; // HH:mm
  type: string; // "Initial" | "Follow-up" | "Consultation" | etc.
  providerName: string;
  status: 'Scheduled' | 'Confirmed' | 'Cancelled';
  dateOfBirth?: string;
  contactDetails?: string;
  reasonForVisit?: string;
} 