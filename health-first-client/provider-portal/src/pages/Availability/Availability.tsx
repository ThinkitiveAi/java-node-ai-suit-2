import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Container,
  Paper,
  Alert,
  CircularProgress,
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { Toaster } from 'react-hot-toast';
import ProviderAvailabilityModal from '../../components/ProviderAvailabilityModal';
import AvailabilityList from '../../components/AvailabilityList';
import { ProviderAvailability } from '../../types/availability';
import { availabilityApi } from '../../services/availabilityApi';
import { sampleAvailabilityData } from '../../utils/sampleData';

const Availability: React.FC = () => {
  const [availability, setAvailability] = useState<ProviderAvailability | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAvailability, setEditingAvailability] = useState<ProviderAvailability | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load availability on component mount
  useEffect(() => {
    loadAvailability();
  }, []);

  const loadAvailability = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // For now, we'll use localStorage to simulate API calls
      // In production, this would be: const data = await availabilityApi.getAvailability();
      const storedAvailability = localStorage.getItem('provider-availability');
      if (storedAvailability) {
        const parsedAvailability = JSON.parse(storedAvailability);
        setAvailability(parsedAvailability);
      } else {
        // Load sample data for demonstration
        setAvailability(sampleAvailabilityData[0]);
        localStorage.setItem('provider-availability', JSON.stringify(sampleAvailabilityData[0]));
      }
    } catch (err) {
      console.error('Error loading availability:', err);
      setError('Failed to load availability. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = () => {
    setEditingAvailability(null);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingAvailability(null);
  };

  const handleEditAvailability = (availabilityToEdit: ProviderAvailability) => {
    setEditingAvailability(availabilityToEdit);
    setIsModalOpen(true);
  };

  const handleSaveAvailability = async (newAvailability: ProviderAvailability) => {
    try {
      // For now, we'll use localStorage to simulate API calls
      // In production, this would be: await availabilityApi.saveAvailability(newAvailability);
      
      // Save to localStorage (simulate API)
      localStorage.setItem('provider-availability', JSON.stringify(newAvailability));
      
      setAvailability(newAvailability);
      handleCloseModal();
    } catch (err) {
      console.error('Error saving availability:', err);
      throw err; // Let the modal handle the error display
    }
  };

  const handleDeleteAvailability = async (id: string) => {
    try {
      // For now, we'll use localStorage to simulate API calls
      // In production, this would be: await availabilityApi.deleteAvailability(id);
      
      // Clear availability
      localStorage.removeItem('provider-availability');
      setAvailability(null);
    } catch (err) {
      console.error('Error deleting availability:', err);
      // You could show a toast error here
    }
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
    <Container maxWidth="lg" sx={{ py: 4 }}>
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

      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 600 }}>
            Provider Availability
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your working hours and availability for patient appointments
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleOpenModal}
          sx={{
            borderRadius: 2,
            px: 3,
            py: 1.5,
            textTransform: 'none',
            fontWeight: 500,
            boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          Set Availability
        </Button>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Availability List */}
      <Paper
        sx={{
          p: 3,
          borderRadius: 2,
          boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.1)',
        }}
      >
        <AvailabilityList
          availability={availability ? [availability] : []}
          onEdit={handleEditAvailability}
          onDelete={handleDeleteAvailability}
        />
      </Paper>

      {/* Availability Modal */}
      <ProviderAvailabilityModal
        open={isModalOpen}
        onClose={handleCloseModal}
        onSave={handleSaveAvailability}
        existingAvailability={editingAvailability}
      />
    </Container>
  );
};

export default Availability; 