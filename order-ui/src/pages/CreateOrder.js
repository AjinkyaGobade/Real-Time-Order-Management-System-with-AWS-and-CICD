import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import InputAdornment from '@mui/material/InputAdornment';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import axios from 'axios';

const CreateOrder = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [formData, setFormData] = useState({
    customerName: '',
    orderAmount: '',
    orderDate: new Date().toISOString().split('T')[0],
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Create form data for file upload
      const orderData = new FormData();
      orderData.append('customerName', formData.customerName);
      orderData.append('orderAmount', formData.orderAmount);
      orderData.append('orderDate', formData.orderDate);
      
      if (selectedFile) {
        orderData.append('invoiceFile', selectedFile);
      }

      // Submit order data to backend
      const response = await axios.post('http://localhost:8080/orders', orderData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      setLoading(false);
      navigate(`/orders/${response.data.orderId}`);
    } catch (error) {
      setLoading(false);
      setError(error.response?.data?.message || 'Failed to create order. Please try again.');
      console.error('Error creating order:', error);
    }
  };

  return (
    <div>
      <Typography variant="h4" component="h1" className="page-title">
        Create New Order
      </Typography>

      <Paper className="form-container">
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Customer Name"
                name="customerName"
                value={formData.customerName}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Order Amount"
                name="orderAmount"
                type="number"
                value={formData.orderAmount}
                onChange={handleChange}
                variant="outlined"
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>,
                }}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Order Date"
                name="orderDate"
                type="date"
                value={formData.orderDate}
                onChange={handleChange}
                variant="outlined"
                InputLabelProps={{ shrink: true }}
              />
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle1" gutterBottom>
                Upload Invoice (PDF)
              </Typography>
              <input
                accept="application/pdf"
                type="file"
                onChange={handleFileChange}
                style={{ marginBottom: '16px' }}
              />
              {selectedFile && (
                <Typography variant="body2">
                  Selected file: {selectedFile.name}
                </Typography>
              )}
            </Grid>
          </Grid>

          <Box className="form-actions">
            <Button
              variant="outlined"
              onClick={() => navigate('/')}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading}
              startIcon={loading ? <CircularProgress size={20} /> : null}
            >
              {loading ? 'Creating...' : 'Create Order'}
            </Button>
          </Box>
        </form>
      </Paper>
    </div>
  );
};

export default CreateOrder;