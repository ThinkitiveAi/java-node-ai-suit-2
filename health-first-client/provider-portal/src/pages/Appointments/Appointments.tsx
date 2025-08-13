import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Container,
  Paper,
  Alert,
  CircularProgress,
  Grid,
  Card,
  CardContent,
  Chip,
  IconButton,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Schedule as ScheduleIcon,
} from '@mui/icons-material';
import { Toaster } from 'react-hot-toast';
import BookAppointmentModal from '../../components/BookAppointmentModal';
import ViewAppointmentList from '../../components/ViewAppointmentList';
import { Appointment, Patient, Provider, AppointmentListItem } from '../../types/appointment';
import { sampleAppointments, samplePatients, sampleProviders } from '../../utils/sampleData';

const Appointments: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [providers, setProviders] = useState<Provider[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState<Appointment | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load data on component mount
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Load appointments from localStorage or use sample data
      const storedAppointments = localStorage.getItem('appointments');
      if (storedAppointments) {
        setAppointments(JSON.parse(storedAppointments));
      } else {
        setAppointments(sampleAppointments);
        localStorage.setItem('appointments', JSON.stringify(sampleAppointments));
      }

      // Load patients and providers
      setPatients(samplePatients);
      setProviders(sampleProviders);
    } catch (err) {
      console.error('Error loading data:', err);
      setError('Failed to load appointments. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = () => {
    setEditingAppointment(null);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingAppointment(null);
  };

  const handleEditAppointment = (appointment: Appointment) => {
    setEditingAppointment(appointment);
    setIsModalOpen(true);
  };

  const handleSaveAppointment = async (appointment: Appointment) => {
    try {
      let updatedAppointments;
      
      if (editingAppointment) {
        // Update existing appointment
        updatedAppointments = appointments.map(apt => 
          apt.id === appointment.id ? appointment : apt
        );
      } else {
        // Add new appointment
        updatedAppointments = [appointment, ...appointments];
      }
      
      // Save to localStorage
      localStorage.setItem('appointments', JSON.stringify(updatedAppointments));
      setAppointments(updatedAppointments);
      handleCloseModal();
    } catch (err) {
      console.error('Error saving appointment:', err);
      throw err;
    }
  };

  const handleDeleteAppointment = async (id: string) => {
    try {
      const updatedAppointments = appointments.filter(apt => apt.id !== id);
      localStorage.setItem('appointments', JSON.stringify(updatedAppointments));
      setAppointments(updatedAppointments);
    } catch (err) {
      console.error('Error deleting appointment:', err);
    }
  };

  // Handlers for ViewAppointmentList
  const handleViewAppointment = (appointment: AppointmentListItem) => {
    console.log('View appointment:', appointment);
    // Implement view appointment logic
  };

  const handleEditAppointmentFromList = (appointment: AppointmentListItem) => {
    console.log('Edit appointment from list:', appointment);
    // Implement edit appointment logic
  };

  const handleRescheduleAppointment = (appointment: AppointmentListItem) => {
    console.log('Reschedule appointment:', appointment);
    // Implement reschedule logic
  };

  const handleCancelAppointment = (appointment: AppointmentListItem) => {
    console.log('Cancel appointment:', appointment);
    // Implement cancel logic
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Box sx={{ width: '100%', minHeight: '100vh' }}>
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
        }}
      />

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ m: 2 }}>
          {error}
        </Alert>
      )}

      {/* View Appointment List */}
      <ViewAppointmentList
        onAddAppointment={handleOpenModal}
        onViewAppointment={handleViewAppointment}
        onEditAppointment={handleEditAppointmentFromList}
        onRescheduleAppointment={handleRescheduleAppointment}
        onCancelAppointment={handleCancelAppointment}
      />

      {/* Book Appointment Modal */}
      <BookAppointmentModal
        open={isModalOpen}
        onClose={handleCloseModal}
        onSave={handleSaveAppointment}
        existingAppointment={editingAppointment}
        patients={patients}
        providers={providers}
      />
    </Box>
  );
};

export default Appointments; 