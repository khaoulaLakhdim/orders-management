import React, { useState, useEffect } from 'react';
import {
  Box,
  Tabs,
  Tab,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  Paper,
  TextField,
  Avatar,
  IconButton,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  InputAdornment,
  Chip,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add as AddIcon, Home, MenuBook, ListAlt, People, Search, Logout, Notifications } from '@mui/icons-material';
import { ordersAPI, clientsAPI } from '../api';
import RefreshIcon from '@mui/icons-material/Refresh';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';

const sidebarNav = [
  { icon: <Home />, label: 'Accueil' },
  { icon: <MenuBook />, label: 'Catalogue' },
  { icon: <ListAlt />, label: 'Mes commandes', active: true },
  { icon: <People />, label: 'Gestion des clients' },
  { icon: <Search />, label: 'Recherche externe' },
];

// Status color and label mapping
const colorMap = {
  COMPLETED: '#22C55E',
  PENDING: '#9CA3AF',
  IN_PROGRESS: '#60A5FA',
};
const statusLabelMap = {
  COMPLETED: 'Clôturée',
  PENDING: 'Non assignée',
  IN_PROGRESS: 'En cours',
};

const Orders = () => {
  const [tabValue, setTabValue] = useState(0);
  const [orders, setOrders] = useState([]);
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 24,
  });
  const [totalRows, setTotalRows] = useState(0);
  const [selectedClientId, setSelectedClientId] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    const fetchClients = async () => {
      try {
        const response = await clientsAPI.getAll();
        let data = response.data;
        if (typeof data === 'string') data = JSON.parse(data);
        if (data.success) setClients(data.clients || []);
      } catch {}
    };
    fetchClients();
  }, []);

  useEffect(() => {
    const fetchOrders = async () => {
      setLoading(true);
      try {
        const params = {
          page: paginationModel.page,
          size: paginationModel.pageSize,
          ...(selectedClientId && { clientId: selectedClientId }),
        };
        const response = await ordersAPI.getAll(params);
        let data = response.data;
        if (typeof data === 'string') data = JSON.parse(data);
        if (data.success) {
          const transformedOrders = (data.orders || []).map(order => ({
            id: order.id,
            date: order.orderDate,
            clientName: order.clientName || 'Unknown Client',
            type: order.type,
            payment: order.paymentMethod,
            shipping: order.expedition,
            amount: order.price,
            status: order.status,
          }));
          setOrders(transformedOrders);
          setTotalRows(data.totalElements || 0);
        } else {
          setOrders([]);
          setTotalRows(0);
        }
      } catch {
        setOrders([]);
        setTotalRows(0);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, [paginationModel, selectedClientId]);

  const columns = [
    { field: 'id', headerName: 'REFERENCE', width: 160 },
    {
      field: 'date',
      headerName: 'DATE',
      width: 120,
      valueFormatter: (params) => {
        if (!params.value) return '';
        if (/^\d{4}-\d{2}-\d{2}$/.test(params.value)) {
          const [y, m, d] = params.value.split('-');
          return `${d}/${m}/${y.slice(2)}`;
        }
        const date = new Date(params.value);
        return isNaN(date) ? params.value : date.toLocaleDateString('fr-FR');
      },
    },
    { field: 'clientName', headerName: 'CLIENT', width: 180 },
    { field: 'type', headerName: 'TYPE', width: 80 },
    { field: 'payment', headerName: 'MOYEN DE PAIMENT', width: 150 },
    { field: 'shipping', headerName: 'EXPEDITION', width: 120 },
    {
      field: 'amount',
      headerName: 'MONTANT',
      width: 120,
      valueFormatter: (params) => {
        if (!params.value) return '';
        return Number(params.value).toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
      },
    },
    {
      field: 'status',
      headerName: 'STATUS',
      width: 120,
      renderCell: (params) => (
        <Chip
          label={statusLabelMap[params.value] || params.value}
          sx={{
            backgroundColor: colorMap[params.value] || '#9CA3AF',
            color: '#fff',
            borderRadius: 2,
            height: 32,
            px: 1.5,
            fontWeight: 600,
            whiteSpace: 'nowrap',
          }}
        />
      ),
    },
    {
      field: 'actions',
      headerName: '',
      width: 60,
      sortable: false,
      renderCell: () => (
        <IconButton size="small">
          <span style={{ fontSize: 24, color: '#bdbdbd' }}>⋮</span>
        </IconButton>
      ),
    },
  ];

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', background: '#f7f9fb' }}>
      {/* Sidebar */}
      <Box sx={{ width: 260, background: '#2471c8', color: 'white', display: 'flex', flexDirection: 'column', alignItems: 'center', py: 4, borderTopLeftRadius: 16, borderBottomLeftRadius: 16 }}>
        {/* Logo */}
        <Box sx={{ mb: 6, width: '100%', px: 3 }}>
          <Typography
            variant="h4"
            sx={{ color: 'white', fontWeight: 500, fontSize: '1.6rem', mb: 2, letterSpacing: 0.5 }}
          >
            <span style={{ fontWeight: 600 }}>Copima Sales</span>
          </Typography>
        </Box>
        {/* Navigation */}
        <List sx={{ width: '100%' }}>
          {sidebarNav.map((item, idx) => (
            <ListItem button key={item.label} sx={{ color: 'white', fontSize: '1.05rem', fontWeight: 400, py: 1.2 }}>
              <ListItemIcon sx={{ color: 'white', minWidth: 36 }}>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} primaryTypographyProps={{ fontSize: '1.05rem', fontWeight: 400 }} />
            </ListItem>
          ))}
        </List>
        <Box sx={{ flexGrow: 1 }} />
        <Button startIcon={<Logout />} sx={{ color: 'white', mb: 2, mt: 4, borderRadius: 2, px: 2, py: 1, background: 'rgba(255,255,255,0.08)' }}>
          Se déconnecter
        </Button>
      </Box>
      {/* Main Content */}
      <Box sx={{ flex: 1, p: 0, display: 'flex', flexDirection: 'column' }}>
        {/* Top Bar */}
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', height: 80, px: 3, mb: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', flex: 1 }}>
            <TextField
              placeholder="Recherche..."
              size="small"
              variant="outlined"
              sx={{ width: 320, background: '#f7f9fb', borderRadius: 2, mr: 3 }}
              value={search}
              onChange={e => setSearch(e.target.value)}
              InputProps={{ startAdornment: <Search sx={{ color: '#bdbdbd', mr: 1 }} /> }}
            />
            <IconButton sx={{ mr: 2 }}><Notifications /></IconButton>
            <Avatar sx={{ bgcolor: '#2471c8', width: 44, height: 44, mr: 2 }}>A</Avatar>
            <Box>
              <Typography sx={{ fontWeight: 700 }}>Abdeslam</Typography>
              <Typography variant="caption" color="text.secondary">Sales - BO</Typography>
            </Box>
          </Box>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            sx={{ borderRadius: 20, height: 40, textTransform: 'none', ml: 2 }}
          >
            Créer une nouvelle commande
          </Button>
        </Box>
        {/* Tabs and filters in a rounded Paper */}
        <Paper elevation={0} sx={{ backgroundColor: '#F5F7FA', borderRadius: 2, p: 2, mb: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
            <Tabs
              value={tabValue}
              onChange={(e, v) => setTabValue(v)}
              TabIndicatorProps={{
                sx: {
                  backgroundColor: '#006BB3',
                  height: 4,
                  borderRadius: 4,
                  bottom: 0,
                }
              }}
              sx={{ mb: 2 }}
            >
              <Tab label="Mes Commandes" sx={{ textTransform: 'none', fontWeight: tabValue===0 ? 600 : 500, color: tabValue===0 ? '#006BB3' : '#7D7D7D' }} />
              <Tab label="Mes Commandes Brouillons" sx={{ textTransform: 'none', fontWeight: tabValue===1 ? 600 : 500, color: tabValue===1 ? '#006BB3' : '#7D7D7D' }} />
              <Tab label="Mes Commandes Annulées" sx={{ textTransform: 'none', fontWeight: tabValue===2 ? 600 : 500, color: tabValue===2 ? '#006BB3' : '#7D7D7D' }} />
              <Tab label="Mes Commandes Bloquées" sx={{ textTransform: 'none', fontWeight: tabValue===3 ? 600 : 500, color: tabValue===3 ? '#006BB3' : '#7D7D7D' }} />
            </Tabs>
            <FormControl size="small" sx={{ minWidth: 140, mr: 2 }}>
              <Select displayEmpty sx={{ backgroundColor: '#fff', borderRadius: 2, height: 40 }}>
                <MenuItem value="">Client</MenuItem>
                {clients.map(client => (
                  <MenuItem key={client.id} value={client.id}>{client.name}</MenuItem>
                ))}
              </Select>
            </FormControl>
            <FormControl size="small" sx={{ minWidth: 140, mr: 2 }}>
              <Select displayEmpty sx={{ backgroundColor: '#fff', borderRadius: 2, height: 40 }}>
                <MenuItem value="">Expedition</MenuItem>
                <MenuItem value="">Tous</MenuItem>
              </Select>
            </FormControl>
            <FormControl size="small" sx={{ minWidth: 140, mr: 2 }}>
              <Select displayEmpty sx={{ backgroundColor: '#fff', borderRadius: 2, height: 40 }}>
                <MenuItem value="">Mode de paiement</MenuItem>
                <MenuItem value="">Tous</MenuItem>
              </Select>
            </FormControl>
            <FormControl size="small" sx={{ minWidth: 140, mr: 2 }}>
              <Select displayEmpty sx={{ backgroundColor: '#fff', borderRadius: 2, height: 40 }}>
                <MenuItem value="">Ville</MenuItem>
                <MenuItem value="">Toutes</MenuItem>
              </Select>
            </FormControl>
            <FormControl size="small" sx={{ minWidth: 140, mr: 2 }}>
              <Select displayEmpty sx={{ backgroundColor: '#fff', borderRadius: 2, height: 40 }}>
                <MenuItem value="">Statut</MenuItem>
                <MenuItem value="">Tous</MenuItem>
              </Select>
            </FormControl>
            <TextField
              type="date"
              size="small"
              sx={{ width: 140, backgroundColor: '#fff', borderRadius: 2, mr: 2, '& .MuiInputBase-root': { height: 40 } }}
              InputProps={{
                endAdornment: <InputAdornment position="end"><CalendarTodayIcon /></InputAdornment>
              }}
            />
            <IconButton sx={{ ml: 'auto', color: '#006BB3', background: '#fff', borderRadius: 2, height: 40, width: 40 }}>
              <RefreshIcon />
            </IconButton>
          </Box>
        </Paper>
        {/* Table */}
        <Box sx={{ flex: 1, px: 4, pb: 4, background: '#f7f9fb' }}>
          <Paper elevation={1} sx={{ borderRadius: 3, overflow: 'hidden' }}>
            <DataGrid
              rows={orders}
              columns={columns}
              pageSize={paginationModel.pageSize}
              rowsPerPageOptions={[24, 48, 96]}
              rowCount={totalRows}
              pagination
              paginationMode="server"
              onPageChange={page => setPaginationModel(prev => ({ ...prev, page }))}
              onPageSizeChange={pageSize => setPaginationModel(prev => ({ ...prev, pageSize }))}
              loading={loading}
              autoHeight={false}
              headerHeight={56}
              rowHeight={52}
              sx={{
                border: 'none',
                fontSize: '1rem',
                background: 'white',
                minHeight: 500,
                '& .MuiDataGrid-columnHeaders': {
                  backgroundColor: '#E8EAED',
                  color: '#7D7D7D',
                  borderBottom: 'none',
                  fontWeight: 600,
                  textTransform: 'uppercase',
                  fontSize: '0.97rem',
                },
                '& .MuiDataGrid-cell': {
                  fontSize: '0.97rem',
                },
                '& .MuiDataGrid-row:hover': {
                  backgroundColor: 'rgba(0, 107, 179, 0.04)',
                },
              }}
            />
          </Paper>
        </Box>
      </Box>
    </Box>
  );
};

export default Orders; 