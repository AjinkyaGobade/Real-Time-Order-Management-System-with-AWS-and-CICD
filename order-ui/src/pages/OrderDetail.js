import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import DownloadIcon from '@mui/icons-material/Download';
import axios from 'axios';

const OrderDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchOrderDetails = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/orders/${id}`);
        setOrder(response.data);
        setLoading(false);
      } catch (error) {
        setError('Failed to load order details. Please try again.');
        setLoading(false);
        console.error('Error fetching order details:', error);
      }
    };

    fetchOrderDetails();
  }, [id]);

  const handleDownloadInvoice = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/orders/${id}/invoice`, {
        responseType: 'blob',
      });
      
      // Create a blob URL for the PDF
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      
      // Create a temporary link and trigger download
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `invoice-${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      
      // Clean up
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error downloading invoice:', error);
      alert('Failed to download invoice. Please try again.');
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 3 }}>
        {error}
      </Alert>
    );
  }

  if (!order) {
    return (
      <Alert severity="warning" sx={{ mt: 3 }}>
        Order not found
      </Alert>
    );
  }

  return (
    <div>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/')}
          sx={{ mr: 2 }}
        >
          Back to Dashboard
        </Button>
        <Typography variant="h4" component="h1">
          Order Details
        </Typography>
      </Box>

      <Paper className="detail-card">
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <div className="detail-section">
              <Typography variant="h6" className="detail-section-title">
                Order ID
              </Typography>
              <Typography variant="body1">{order.orderId}</Typography>
            </div>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <div className="detail-section">
              <Typography variant="h6" className="detail-section-title">
                Order Date
              </Typography>
              <Typography variant="body1">
                {new Date(order.orderDate).toLocaleDateString()}
              </Typography>
            </div>
          </Grid>
          
          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
          </Grid>
          
          <Grid item xs={12} md={6}>
            <div className="detail-section">
              <Typography variant="h6" className="detail-section-title">
                Customer Name
              </Typography>
              <Typography variant="body1">{order.customerName}</Typography>
            </div>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <div className="detail-section">
              <Typography variant="h6" className="detail-section-title">
                Order Amount
              </Typography>
              <Typography variant="body1">
                ${parseFloat(order.orderAmount).toFixed(2)}
              </Typography>
            </div>
          </Grid>
          
          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
          </Grid>
          
          <Grid item xs={12}>
            <div className="detail-section">
              <Typography variant="h6" className="detail-section-title">
                Invoice
              </Typography>
              {order.invoiceFileUrl ? (
                <Button
                  variant="contained"
                  color="primary"
                  startIcon={<DownloadIcon />}
                  onClick={handleDownloadInvoice}
                >
                  Download Invoice
                </Button>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  No invoice available for this order
                </Typography>
              )}
            </div>
          </Grid>
        </Grid>
      </Paper>
    </div>
  );
};

export default OrderDetail;