import React, { useState } from 'react'
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material'
import { Add as AddIcon } from '@mui/icons-material'

const Schedule = () => {
  const [openDialog, setOpenDialog] = useState(false)

  const scheduleData = {
    'Monday': [
      { time: '09:00 AM', patient: 'John Doe', type: 'Follow-up' },
      { time: '10:30 AM', patient: 'Jane Smith', type: 'Consultation' },
      { time: '02:00 PM', patient: 'Mike Johnson', type: 'Emergency' },
    ],
    'Tuesday': [
      { time: '08:00 AM', patient: 'Sarah Wilson', type: 'Checkup' },
      { time: '11:00 AM', patient: 'David Brown', type: 'Follow-up' },
    ],
    'Wednesday': [
      { time: '09:30 AM', patient: 'Emily Davis', type: 'Consultation' },
      { time: '01:00 PM', patient: 'Robert Taylor', type: 'Follow-up' },
    ],
    'Thursday': [
      { time: '10:00 AM', patient: 'Lisa Anderson', type: 'Checkup' },
    ],
    'Friday': [
      { time: '08:30 AM', patient: 'James Wilson', type: 'Emergency' },
      { time: '11:30 AM', patient: 'Mary Johnson', type: 'Follow-up' },
    ],
  }

  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']

  const handleOpenDialog = () => {
    setOpenDialog(true)
  }

  const handleCloseDialog = () => {
    setOpenDialog(false)
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Weekly Schedule</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleOpenDialog}
        >
          Add Time Slot
        </Button>
      </Box>

      <Grid container spacing={3}>
        {days.map((day) => (
          <Grid item xs={12} md={6} lg={4} key={day}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {day}
              </Typography>
              {scheduleData[day] && scheduleData[day].length > 0 ? (
                scheduleData[day].map((appointment, index) => (
                  <Card key={index} sx={{ mb: 1 }}>
                    <CardContent sx={{ py: 1 }}>
                      <Typography variant="subtitle2" color="primary">
                        {appointment.time}
                      </Typography>
                      <Typography variant="body2">
                        {appointment.patient}
                      </Typography>
                      <Typography variant="caption" color="textSecondary">
                        {appointment.type}
                      </Typography>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <Typography variant="body2" color="textSecondary">
                  No appointments scheduled
                </Typography>
              )}
            </Paper>
          </Grid>
        ))}
      </Grid>

      {/* Add Time Slot Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Add Time Slot</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Day</InputLabel>
                <Select label="Day">
                  {days.map((day) => (
                    <MenuItem key={day} value={day}>
                      {day}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Time"
                type="time"
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Patient Name"
                placeholder="Enter patient name"
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Appointment Type</InputLabel>
                <Select label="Appointment Type">
                  <MenuItem value="consultation">Consultation</MenuItem>
                  <MenuItem value="follow-up">Follow-up</MenuItem>
                  <MenuItem value="checkup">Checkup</MenuItem>
                  <MenuItem value="emergency">Emergency</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Notes"
                multiline
                rows={3}
                placeholder="Additional notes..."
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button variant="contained" onClick={handleCloseDialog}>
            Add Slot
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default Schedule 