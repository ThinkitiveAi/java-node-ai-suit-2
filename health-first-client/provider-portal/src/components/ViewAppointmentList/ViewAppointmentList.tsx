import React, { useState, useEffect, useMemo } from 'react';
import {
  Box,
  Typography,
  Button,
  TextField,
  InputAdornment,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  TablePagination,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Avatar,
  CircularProgress,
  Alert,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
  Edit as EditIcon,
  Visibility as VisibilityIcon,
  Schedule as ScheduleIcon,
  Sort as SortIcon,
  KeyboardArrowUp as ArrowUpIcon,
  KeyboardArrowDown as ArrowDownIcon,
} from '@mui/icons-material';
import { AppointmentListItem } from '../../types/appointment';
import { sampleAppointmentList } from '../../utils/sampleData';

interface ViewAppointmentListProps {
  onAddAppointment?: () => void;
  onViewAppointment?: (appointment: AppointmentListItem) => void;
  onEditAppointment?: (appointment: AppointmentListItem) => void;
  onRescheduleAppointment?: (appointment: AppointmentListItem) => void;
  onCancelAppointment?: (appointment: AppointmentListItem) => void;
}

type SortField = 'date' | 'patientName' | 'status';
type SortDirection = 'asc' | 'desc';

const ViewAppointmentList: React.FC<ViewAppointmentListProps> = ({
  onAddAppointment,
  onViewAppointment,
  onEditAppointment,
  onRescheduleAppointment,
  onCancelAppointment,
}) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  // State
  const [appointments, setAppointments] = useState<AppointmentListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchFilter, setSearchFilter] = useState<'all' | 'patient' | 'provider'>('all');
  const [sortField, setSortField] = useState<SortField>('date');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  // Load appointments on mount
  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Load from localStorage or use sample data
      const storedAppointments = localStorage.getItem('appointment-list');
      if (storedAppointments) {
        setAppointments(JSON.parse(storedAppointments));
      } else {
        setAppointments(sampleAppointmentList);
        localStorage.setItem('appointment-list', JSON.stringify(sampleAppointmentList));
      }
    } catch (err) {
      console.error('Error loading appointments:', err);
      setError('Failed to load appointments. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Debounced search
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState(searchTerm);
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm);
    }, 300);

    return () => clearTimeout(timer);
  }, [searchTerm]);

  // Filter and sort appointments
  const filteredAndSortedAppointments = useMemo(() => {
    let filtered = appointments.filter(appointment => {
      const searchLower = debouncedSearchTerm.toLowerCase();
      
      switch (searchFilter) {
        case 'patient':
          return appointment.patientName.toLowerCase().includes(searchLower);
        case 'provider':
          return appointment.providerName.toLowerCase().includes(searchLower);
        default:
          return (
            appointment.patientName.toLowerCase().includes(searchLower) ||
            appointment.providerName.toLowerCase().includes(searchLower) ||
            appointment.type.toLowerCase().includes(searchLower) ||
            appointment.reasonForVisit?.toLowerCase().includes(searchLower)
          );
      }
    });

    // Sort appointments
    filtered.sort((a, b) => {
      let aValue: string | number;
      let bValue: string | number;

      switch (sortField) {
        case 'date':
          aValue = new Date(`${a.date} ${a.time}`).getTime();
          bValue = new Date(`${b.date} ${b.time}`).getTime();
          break;
        case 'patientName':
          aValue = a.patientName.toLowerCase();
          bValue = b.patientName.toLowerCase();
          break;
        case 'status':
          aValue = a.status.toLowerCase();
          bValue = b.status.toLowerCase();
          break;
        default:
          return 0;
      }

      if (sortDirection === 'asc') {
        return aValue > bValue ? 1 : -1;
      } else {
        return aValue < bValue ? 1 : -1;
      }
    });

    return filtered;
  }, [appointments, debouncedSearchTerm, searchFilter, sortField, sortDirection]);

  // Pagination
  const paginatedAppointments = useMemo(() => {
    const startIndex = page * rowsPerPage;
    return filteredAndSortedAppointments.slice(startIndex, startIndex + rowsPerPage);
  }, [filteredAndSortedAppointments, page, rowsPerPage]);

  // Handle sort
  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
    setPage(0);
  };

  // Handle pagination
  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  // Get status color
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Scheduled':
        return { bg: '#F6F8FB', text: '#5980BF', border: '#5980BF' };
      case 'Confirmed':
        return { bg: '#FEFAFF', text: '#E8A6FF', border: '#E8A6FF' };
      case 'Cancelled':
        return { bg: '#FEF2F2', text: '#EC2020', border: '#EC2020' };
      default:
        return { bg: '#F6F8FB', text: '#5980BF', border: '#5980BF' };
    }
  };

  // Format date and time
  const formatDateTime = (date: string, time: string) => {
    const dateObj = new Date(`${date} ${time}`);
    return dateObj.toLocaleDateString('en-US', {
      month: '2-digit',
      day: '2-digit',
      year: '2-digit',
    }) + ', ' + dateObj.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true,
    });
  };

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="400px"
        width="100%"
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ width: '100%', backgroundColor: '#F3F3F3', minHeight: '100vh' }}>
      {/* Header */}
      <Box
        sx={{
          backgroundColor: '#FFFFFF',
          boxShadow: '1px 1px 8px 0px rgba(0, 0, 0, 0.25)',
          borderRadius: '4px',
          margin: '12px',
          padding: '16px',
        }}
      >
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '16px',
          }}
        >
          {/* Left side - Search and filter */}
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <Box sx={{ position: 'relative' }}>
              <TextField
                placeholder="Search by patient name, DOB"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                sx={{
                  width: 250,
                  '& .MuiOutlinedInput-root': {
                    backgroundColor: '#FFFFFF',
                    boxShadow: '0px 0px 8px 0px rgba(0, 0, 0, 0.16)',
                    borderRadius: '4px',
                    '& fieldset': {
                      border: 'none',
                    },
                  },
                }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon sx={{ color: 'rgba(26, 26, 26, 0.5)' }} />
                    </InputAdornment>
                  ),
                }}
              />
            </Box>
            
            <FormControl size="small" sx={{ minWidth: 120 }}>
              <InputLabel>Filter by</InputLabel>
              <Select
                value={searchFilter}
                onChange={(e) => setSearchFilter(e.target.value as any)}
                label="Filter by"
                sx={{
                  backgroundColor: '#FFFFFF',
                  '& .MuiOutlinedInput-root': {
                    borderRadius: '4px',
                  },
                }}
              >
                <MenuItem value="all">All</MenuItem>
                <MenuItem value="patient">Patient Name</MenuItem>
                <MenuItem value="provider">Provider Name</MenuItem>
              </Select>
            </FormControl>
          </Box>

          {/* Right side - Add Appointment button */}
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={onAddAppointment}
            sx={{
              backgroundColor: '#233853',
              borderRadius: '4px',
              textTransform: 'none',
              fontWeight: 700,
              fontSize: '12px',
              padding: '10px 16px',
              '&:hover': {
                backgroundColor: '#1a2a3f',
              },
            }}
          >
            Schedule Appointment
          </Button>
        </Box>

        {/* Error Alert */}
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
      </Box>

      {/* Table Container */}
      <Box
        sx={{
          backgroundColor: '#FFFFFF',
          boxShadow: '1px 1px 8px 0px rgba(0, 0, 0, 0.25)',
          borderRadius: '4px',
          margin: '12px',
          overflow: 'hidden',
        }}
      >
        <TableContainer sx={{ maxHeight: 'calc(100vh - 200px)' }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                {/* Date & Time Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    cursor: 'pointer',
                    minWidth: 193,
                  }}
                  onClick={() => handleSort('date')}
                >
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    Date & Time
                    {sortField === 'date' ? (
                      sortDirection === 'asc' ? <ArrowUpIcon fontSize="small" /> : <ArrowDownIcon fontSize="small" />
                    ) : (
                      <SortIcon fontSize="small" sx={{ opacity: 0.5 }} />
                    )}
                  </Box>
                </TableCell>

                {/* Appointment Type Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    minWidth: 166,
                  }}
                >
                  Appointment Type
                </TableCell>

                {/* Patient Name Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    cursor: 'pointer',
                    minWidth: 218,
                  }}
                  onClick={() => handleSort('patientName')}
                >
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    Patient Name
                    {sortField === 'patientName' ? (
                      sortDirection === 'asc' ? <ArrowUpIcon fontSize="small" /> : <ArrowDownIcon fontSize="small" />
                    ) : (
                      <SortIcon fontSize="small" sx={{ opacity: 0.5 }} />
                    )}
                  </Box>
                </TableCell>

                {/* Date of Birth Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    minWidth: 138,
                  }}
                >
                  Date of Birth
                </TableCell>

                {/* Contact Details Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    minWidth: 125,
                  }}
                >
                  Contact Details
                </TableCell>

                {/* Provider Name Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    minWidth: 138,
                  }}
                >
                  Provider Name
                </TableCell>

                {/* Reason for Visit Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    minWidth: 137,
                  }}
                >
                  Reason for Visit
                </TableCell>

                {/* Status Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    cursor: 'pointer',
                    minWidth: 137,
                  }}
                  onClick={() => handleSort('status')}
                >
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    Status
                    {sortField === 'status' ? (
                      sortDirection === 'asc' ? <ArrowUpIcon fontSize="small" /> : <ArrowDownIcon fontSize="small" />
                    ) : (
                      <SortIcon fontSize="small" sx={{ opacity: 0.5 }} />
                    )}
                  </Box>
                </TableCell>

                {/* Actions Column */}
                <TableCell
                  sx={{
                    backgroundColor: '#E7E7E7',
                    color: '#565656',
                    fontWeight: 500,
                    fontSize: '14px',
                    padding: '12px',
                    minWidth: 200,
                  }}
                >
                  Action
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedAppointments.map((appointment) => (
                <TableRow key={appointment.id} hover>
                  {/* Date & Time */}
                  <TableCell sx={{ padding: '12px', color: '#727272', fontSize: '14px' }}>
                    {formatDateTime(appointment.date, appointment.time)}
                  </TableCell>

                  {/* Appointment Type */}
                  <TableCell sx={{ padding: '12px', color: '#727272', fontSize: '14px' }}>
                    {appointment.type}
                  </TableCell>

                  {/* Patient Name */}
                  <TableCell sx={{ padding: '12px' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Avatar
                        sx={{
                          width: 24,
                          height: 24,
                          fontSize: '12px',
                          backgroundColor: '#233853',
                        }}
                      >
                        {appointment.patientName.charAt(0)}
                      </Avatar>
                      <Typography sx={{ color: '#233853', fontWeight: 500, fontSize: '14px' }}>
                        {appointment.patientName}
                      </Typography>
                    </Box>
                  </TableCell>

                  {/* Date of Birth */}
                  <TableCell sx={{ padding: '12px', color: '#727272', fontSize: '14px' }}>
                    {appointment.dateOfBirth}
                  </TableCell>

                  {/* Contact Details */}
                  <TableCell sx={{ padding: '12px', color: '#727272', fontSize: '14px' }}>
                    {appointment.contactDetails}
                  </TableCell>

                  {/* Provider Name */}
                  <TableCell sx={{ padding: '12px', color: '#727272', fontSize: '14px' }}>
                    {appointment.providerName}
                  </TableCell>

                  {/* Reason for Visit */}
                  <TableCell sx={{ padding: '12px', color: '#8C8C8C', fontSize: '14px' }}>
                    {appointment.reasonForVisit}
                  </TableCell>

                  {/* Status */}
                  <TableCell sx={{ padding: '12px' }}>
                    <Chip
                      label={appointment.status}
                      size="small"
                      sx={{
                        backgroundColor: getStatusColor(appointment.status).bg,
                        color: getStatusColor(appointment.status).text,
                        border: `1px solid ${getStatusColor(appointment.status).border}`,
                        borderRadius: '100px',
                        fontSize: '12px',
                        fontWeight: 500,
                        height: '24px',
                      }}
                    />
                  </TableCell>

                  {/* Actions */}
                  <TableCell sx={{ padding: '12px' }}>
                    <Box sx={{ display: 'flex', gap: 1 }}>
                      <Button
                        size="small"
                        variant="outlined"
                        startIcon={<VisibilityIcon />}
                        onClick={() => onViewAppointment?.(appointment)}
                        sx={{
                          backgroundColor: '#E5F0FF',
                          borderColor: '#233853',
                          color: '#233853',
                          borderRadius: '4px',
                          fontSize: '13px',
                          fontWeight: 500,
                          padding: '4px 6px',
                          minWidth: 'auto',
                          '&:hover': {
                            backgroundColor: '#D0E0FF',
                            borderColor: '#233853',
                          },
                        }}
                      >
                        Start
                      </Button>
                      
                      <Box
                        sx={{
                          width: '1px',
                          backgroundColor: '#F0F0F0',
                          margin: '0 4px',
                        }}
                      />
                      
                      <Button
                        size="small"
                        variant="outlined"
                        startIcon={<EditIcon />}
                        onClick={() => onEditAppointment?.(appointment)}
                        sx={{
                          backgroundColor: '#E5F0FF',
                          borderColor: '#233853',
                          color: '#233853',
                          borderRadius: '4px',
                          fontSize: '13px',
                          fontWeight: 500,
                          padding: '4px 6px',
                          minWidth: 'auto',
                          '&:hover': {
                            backgroundColor: '#D0E0FF',
                            borderColor: '#233853',
                          },
                        }}
                      >
                        Edit
                      </Button>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Pagination */}
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '4px 16px',
            borderTop: '1px solid #E9E9E9',
          }}
        >
          <Typography sx={{ color: '#464646', fontSize: '12px' }}>
            Showing {page * rowsPerPage + 1} to {Math.min((page + 1) * rowsPerPage, filteredAndSortedAppointments.length)} of {filteredAndSortedAppointments.length} entries
          </Typography>
          
          <TablePagination
            component="div"
            count={filteredAndSortedAppointments.length}
            page={page}
            onPageChange={handleChangePage}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={handleChangeRowsPerPage}
            rowsPerPageOptions={[10, 25, 50]}
            sx={{
              '& .MuiTablePagination-selectLabel, & .MuiTablePagination-displayedRows': {
                fontSize: '12px',
                color: '#464646',
              },
            }}
          />
        </Box>
      </Box>
    </Box>
  );
};

export default ViewAppointmentList; 