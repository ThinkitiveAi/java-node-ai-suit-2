import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Chip,
  IconButton,
  Grid,
  Paper,
  Divider,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Schedule as ScheduleIcon,
  AccessTime as AccessTimeIcon,
  Language as LanguageIcon,
  Block as BlockIcon,
} from '@mui/icons-material';
import { ProviderAvailability } from '../../types/availability';

interface AvailabilityListProps {
  availability: ProviderAvailability[];
  onEdit?: (availability: ProviderAvailability) => void;
  onDelete?: (id: string) => void;
}

const AvailabilityList: React.FC<AvailabilityListProps> = ({
  availability,
  onEdit,
  onDelete,
}) => {
  if (availability.length === 0) {
    return (
      <Paper
        sx={{
          p: 4,
          textAlign: 'center',
          backgroundColor: 'grey.50',
          borderRadius: 2,
        }}
      >
        <ScheduleIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h6" color="text.secondary" gutterBottom>
          No Availability Set
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Click "Set Availability" to configure your working hours
        </Typography>
      </Paper>
    );
  }

  const formatTime = (time: string) => {
    const [hours, minutes] = time.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
  };

  const getTimezoneLabel = (timezone: string) => {
    const timezoneMap: Record<string, string> = {
      'America/New_York': 'Eastern Time (ET)',
      'America/Chicago': 'Central Time (CT)',
      'America/Denver': 'Mountain Time (MT)',
      'America/Los_Angeles': 'Pacific Time (PT)',
      'America/Anchorage': 'Alaska Time (AKT)',
      'Pacific/Honolulu': 'Hawaii Time (HST)',
      'UTC': 'UTC',
      'Europe/London': 'London (GMT)',
      'Europe/Paris': 'Paris (CET)',
      'Asia/Tokyo': 'Tokyo (JST)',
      'Asia/Shanghai': 'Shanghai (CST)',
      'Asia/Kolkata': 'India (IST)',
      'Australia/Sydney': 'Sydney (AEST)',
    };
    return timezoneMap[timezone] || timezone;
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <Box>
      <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 500 }}>
        Current Availability
      </Typography>
      
      <Grid container spacing={3}>
        {availability.map((item) => (
          <Grid item xs={12} key={item.id}>
            <Card
              sx={{
                borderRadius: 2,
                boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.1)',
                transition: 'all 0.2s ease-in-out',
                '&:hover': {
                  boxShadow: '0px 4px 16px rgba(0, 0, 0, 0.15)',
                  transform: 'translateY(-2px)',
                },
              }}
            >
              <CardContent sx={{ p: 3 }}>
                {/* Header with actions */}
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'flex-start',
                    mb: 3,
                  }}
                >
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="h5" gutterBottom sx={{ fontWeight: 600 }}>
                      {item.providerName || 'Provider'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {getTimezoneLabel(item.timezone)}
                    </Typography>
                  </Box>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    {onEdit && (
                      <IconButton
                        size="small"
                        onClick={() => onEdit(item)}
                        sx={{
                          color: 'primary.main',
                          '&:hover': {
                            backgroundColor: 'primary.50',
                          },
                        }}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                    )}
                    {onDelete && (
                      <IconButton
                        size="small"
                        onClick={() => onDelete(item.id)}
                        sx={{
                          color: 'error.main',
                          '&:hover': {
                            backgroundColor: 'error.50',
                          },
                        }}
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    )}
                  </Box>
                </Box>

                <Divider sx={{ mb: 3 }} />

                {/* Settings */}
                <Box sx={{ mb: 3 }}>
                  <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                    Settings
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {item.locationMode && (
                      <Chip
                        label={item.locationMode}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    )}
                    {item.repeatWeekly && (
                      <Chip
                        label="Repeats Weekly"
                        size="small"
                        color="success"
                        variant="outlined"
                      />
                    )}
                  </Box>
                </Box>

                {/* Day Availabilities */}
                <Box sx={{ mb: 3 }}>
                  <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                    Available Days
                  </Typography>
                  <Grid container spacing={2}>
                    {item.dayAvailabilities.map((dayAvail) => (
                      <Grid item xs={12} sm={6} md={4} key={dayAvail.id}>
                        <Box
                          sx={{
                            p: 2,
                            border: '1px solid',
                            borderColor: 'primary.200',
                            borderRadius: 1,
                            backgroundColor: 'primary.50',
                          }}
                        >
                          <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>
                            {dayAvail.day}
                          </Typography>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <AccessTimeIcon fontSize="small" color="action" />
                            <Typography variant="body2">
                              {formatTime(dayAvail.startTime)} - {formatTime(dayAvail.endTime)}
                            </Typography>
                          </Box>
                        </Box>
                      </Grid>
                    ))}
                  </Grid>
                </Box>

                {/* Block Days */}
                {item.blockDays.length > 0 && (
                  <Box sx={{ mb: 3 }}>
                    <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                      Blocked Days
                    </Typography>
                    <Grid container spacing={2}>
                      {item.blockDays.map((blockDay) => (
                        <Grid item xs={12} sm={6} md={4} key={blockDay.id}>
                          <Box
                            sx={{
                              p: 2,
                              border: '1px solid',
                              borderColor: 'error.200',
                              borderRadius: 1,
                              backgroundColor: 'error.50',
                            }}
                          >
                            <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>
                              {formatDate(blockDay.date)}
                            </Typography>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                              <BlockIcon fontSize="small" color="error" />
                              <Typography variant="body2">
                                {formatTime(blockDay.startTime)} - {formatTime(blockDay.endTime)}
                              </Typography>
                            </Box>
                          </Box>
                        </Grid>
                      ))}
                    </Grid>
                  </Box>
                )}

                {/* Notes */}
                {item.notes && (
                  <Box>
                    <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                      Notes
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {item.notes}
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default AvailabilityList; 