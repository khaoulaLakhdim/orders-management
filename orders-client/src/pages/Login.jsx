import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  FormControlLabel,
  Checkbox,
  Grid,
  Container,
  Alert,
} from '@mui/material';
import { authAPI } from '../api';
import engineImg from '../assets/react.svg';

const Login = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    rememberMe: false,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'rememberMe' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = await authAPI.login({
        username: formData.username,
        password: formData.password,
      });
      if (response.data.success) {
        const user = response.data.user;
        localStorage.setItem('authToken', 'authenticated');
        localStorage.setItem('user', JSON.stringify({
          id: user.id,
          name: user.username,
          role: user.role,
        }));
        window.location.reload();
      } else {
        setError(response.data.message || 'Login failed');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Une erreur est survenue lors de la connexion');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: '#f7f9fb',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        p: 2,
      }}
    >
      <Box
        sx={{
          width: '100%',
          maxWidth: 1100,
          background: 'white',
          borderRadius: 4,
          boxShadow: 3,
          overflow: 'hidden',
          display: 'flex',
          minHeight: { xs: 'auto', md: 600 },
        }}
      >
        {/* Left Side */}
        <Box
          sx={{
            flex: 1,
            background: '#eaf0f6',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            p: 4,
            position: 'relative',
          }}
        >
          {/* Logo */}
          <Box sx={{ position: 'absolute', top: 32, left: 32, display: 'flex', alignItems: 'center' }}>
            {/* Replace with <img src={logo} ... /> if you have a logo file */}
            <Typography variant="h5" sx={{ color: 'white', fontWeight: 700, letterSpacing: 1, mr: 1, textShadow: '0 2px 8px #0002' }}>
              Copima Core
            </Typography>
          </Box>
          {/* Image */}
          <Box
            sx={{
              width: { xs: 220, md: 400 },
              height: { xs: 220, md: 400 },
              borderRadius: 3,
              overflow: 'hidden',
              boxShadow: 2,
              mb: 2,
              mt: 6,
            }}
          >
            <img src={engineImg} alt="engine" style={{ width: '100%', height: '100%', objectFit: 'cover', filter: 'brightness(0.8)' }} />
          </Box>
        </Box>
        {/* Right Side - Login Form */}
        <Box
          sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            p: { xs: 3, md: 6 },
          }}
        >
          <Box sx={{ width: '100%', maxWidth: 400 }}>
            <Typography variant="h4" component="h2" gutterBottom align="left" sx={{ fontWeight: 700 }}>
              Se Connecter
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              lorem ipsum dolor sit amet, consectetur adipiscing elit. proin ac commodo ipsum.
            </Typography>
            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}
            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
              <TextField
                fullWidth
                label="Nom d'utilisateur"
                name="username"
                type="text"
                value={formData.username}
                onChange={handleChange}
                margin="normal"
                required
                autoComplete="username"
                autoFocus
              />
              <TextField
                fullWidth
                label="Mot de passe"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                margin="normal"
                required
                autoComplete="current-password"
              />
              <FormControlLabel
                control={
                  <Checkbox
                    name="rememberMe"
                    checked={formData.rememberMe}
                    onChange={handleChange}
                    color="primary"
                  />
                }
                label="Gardez-moi connecté"
                sx={{ mt: 1 }}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={loading}
                sx={{ mt: 3, mb: 2, background: 'linear-gradient(90deg, #1976d2 0%, #2196f3 100%)', color: 'white', fontWeight: 600, fontSize: '1.1rem', boxShadow: 2 }}
              >
                {loading ? 'Connexion...' : 'Se Connecter'}
              </Button>
            </Box>
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default Login; 