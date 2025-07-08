import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#006BB3',
      light: '#4d9bd9',
      dark: '#004a8c',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#f50057',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
  },
  typography: {
    fontFamily: '"Poppins", "Helvetica", "Arial", sans-serif',
    fontSize: 12,
    h4: {
      fontWeight: 500,
      fontSize: '1.1rem',
      letterSpacing: 0,
    },
    h5: {
      fontWeight: 500,
      fontSize: '1rem',
    },
    h6: {
      fontWeight: 500,
      fontSize: '0.95rem',
    },
    subtitle1: {
      fontWeight: 400,
      fontSize: '0.9rem',
    },
    body1: {
      fontWeight: 400,
      fontSize: '0.87rem',
    },
    button: {
      textTransform: 'none',
      fontWeight: 600,
      fontSize: '0.9rem',
      letterSpacing: 0.2,
    },
    caption: {
      fontSize: '0.7rem',
      fontWeight: 400,
    },
  },
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
          padding: '8px 24px',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
          },
        },
      },
    },
  },
});

export default theme; 