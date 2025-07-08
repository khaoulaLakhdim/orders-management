import React, { useState } from 'react';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  TextField,
  Avatar,
  Menu,
  MenuItem,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Home,
  ShoppingCart,
  Receipt,
  People,
  Search,
  Notifications,
  AccountCircle,
  Logout,
} from '@mui/icons-material';
import { authAPI } from '../api';

const drawerWidth = 240;

const menuItems = [
  { text: 'Accueil', icon: <Home />, path: '/' },
  { text: 'Catalogue', icon: <ShoppingCart />, path: '/catalogue' },
  { text: 'Mes Commandes', icon: <Receipt />, path: '/orders' },
  { text: 'Gestion des clients', icon: <People />, path: '/clients' },
  { text: 'Recherche externe', icon: <Search />, path: '/search' },
];

const AppShell = ({ children }) => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [user] = useState(() => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : { name: 'Utilisateur' };
  });

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleUserMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleUserMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Always clear local storage and redirect
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
  };

  const drawer = (
    <Box>
      <Toolbar>
        <Typography variant="h6" noWrap component="div" color="primary">
          Orders App
        </Typography>
      </Toolbar>
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton>
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ flex: 1, minHeight: '100vh', background: '#f7f9fb' }}>
      {children}
    </Box>
  );
};

export default AppShell; 