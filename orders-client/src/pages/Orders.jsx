import React, { useState, useEffect } from 'react';
import {
  Box,
  Tabs,
  Tab,
  Button,
  FormControl,
  Select,
  MenuItem,
  Paper,
  TextField,
  Avatar,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  InputAdornment,
  Chip,
  Typography,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import {
  Add as AddIcon,
  Home,
  MenuBook,
  ListAlt,
  People,
  Search,
  Logout,
  Notifications,
  Refresh as RefreshIcon,
  CalendarToday as CalendarTodayIcon,
} from '@mui/icons-material';
import { ordersAPI, clientsAPI, authAPI } from '../api';

const sidebarNav = [
  { icon: <Home />, label: 'Accueil' },
  { icon: <MenuBook />, label: 'Catalogue' },
  { icon: <ListAlt />, label: 'Mes commandes', active: true },
  { icon: <People />, label: 'Gestion des clients' },
  { icon: <Search />, label: 'Recherche externe' },
];

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

export default function Orders() {
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
  const [selectedExpedition, setSelectedExpedition] = useState('');
  const [selectedPayment, setSelectedPayment] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [minAmount, setMinAmount] = useState('');
  const [maxAmount, setMaxAmount] = useState('');
  const [search, setSearch] = useState('');

  const user = JSON.parse(localStorage.getItem('user') || '{}');

  // fetch clients once
  useEffect(() => {
    clientsAPI.getAll().then(res => {
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      if (data.success) setClients(data.clients || []);
    });
  }, []);

  // fetch orders whenever paginationModel or filters change
  useEffect(() => {
    setLoading(true);
    const params = {
      page: paginationModel.page,
      size: paginationModel.pageSize,
      ...(selectedClientId && { clientId: selectedClientId }),
      ...(selectedExpedition && { expedition: selectedExpedition }),
      ...(selectedPayment && { paymentMethod: selectedPayment }),
      ...(selectedStatus && { status: selectedStatus }),
      ...(minAmount && { minPrice: minAmount }),
      ...(maxAmount && { maxPrice: maxAmount }),
    };

    ordersAPI.getAll(params)
      .then(res => {
        let raw = res.data;
        if (typeof raw === 'string') raw = JSON.parse(raw);

        // handle Spring Page<T> or legacy wrapper
        const items = Array.isArray(raw.content)
          ? raw.content
          : Array.isArray(raw.orders)
            ? raw.orders
            : [];
        const total = typeof raw.totalElements === 'number'
          ? raw.totalElements
          : typeof raw.total === 'number'
            ? raw.total
            : 0;

        const mapped = items.map(o => ({
          id: o.id,
          date: o.orderDate,
          clientName: o.clientName,
          type: o.type,
          payment: o.paymentMethod,
          shipping: o.expedition,
          amount: o.price,
          status: o.status,
        }));

        setOrders(mapped);
        setTotalRows(total);
      })
      .catch(() => {
        setOrders([]);
        setTotalRows(0);
      })
      .finally(() => setLoading(false));
  }, [
    paginationModel,
    selectedClientId,
    selectedExpedition,
    selectedPayment,
    selectedStatus,
    minAmount,
    maxAmount,
  ]);

  const columns = [
    { field: 'id', headerName: 'RÉFÉRENCE', width: 100 },
    { field: 'date', headerName: 'DATE', width: 140 },
    { field: 'clientName', headerName: 'CLIENT', width: 200 },
    { field: 'type', headerName: 'TYPE', width: 80 },
    { field: 'payment', headerName: 'MOYEN DE PAIEMENT', width: 160 },
    { field: 'shipping', headerName: 'EXPÉDITION', width: 120 },
    { field: 'amount', headerName: 'MONTANT', width: 120 },
    {
      field: 'status',
      headerName: 'STATUT',
      width: 140,
      renderCell: params => (
        <Chip
          label={statusLabelMap[params.value] || params.value}
          sx={{
            backgroundColor: colorMap[params.value] || '#9CA3AF',
            color: '#fff',
            borderRadius: 2,
            height: 32,
            px: 1,
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
          <Typography variant="h6" color="text.disabled">⋮</Typography>
        </IconButton>
      ),
    },
  ];

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', background: '#f7f9fb' }}>
      {/* Sidebar */}
      <Box sx={{ width: 260, bgcolor: '#006BB3', color: '#fff', px: 2, py: 4 }}>
        <Typography variant="h5" fontWeight={600} mb={4}>Copima Sales</Typography>
        <List>
          {sidebarNav.map(item => (
            <ListItem button key={item.label} selected={item.active}>
              <ListItemIcon sx={{ color: '#fff' }}>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} primaryTypographyProps={{ color: '#fff' }} />
            </ListItem>
          ))}
        </List>
        <Box flexGrow={1} />
        <Button
          startIcon={<Logout />}
          sx={{ color: '#fff', borderColor: 'rgba(255,255,255,0.5)', borderWidth: 1, borderStyle: 'solid' }}
          onClick={async () => {
            await authAPI.logout();
            window.location.href = '/login';
          }}
        >
          Se déconnecter
        </Button>
      </Box>

      {/* Main Content */}
      <Box flex={1} display="flex" flexDirection="column">
        {/* Top Bar */}
        <Box display="flex" alignItems="center" justifyContent="space-between" px={3} height={80}>
          <TextField
            placeholder="Recherche..."
            size="small"
            variant="outlined"
            value={search}
            onChange={e => setSearch(e.target.value)}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <Search color="disabled" />
                </InputAdornment>
              )
            }}
            sx={{ width: 320, bgcolor: '#f7f9fb', borderRadius: 2 }}
          />
          <Box display="flex" alignItems="center">
            <IconButton><Notifications /></IconButton>
            <Avatar sx={{ bgcolor: '#006BB3', mx: 2 }}>
              {user.name?.[0]?.toUpperCase() || 'U'}
            </Avatar>
            <Box>
              <Typography fontWeight={700}>{user.name}</Typography>
              <Typography variant="caption" color="text.secondary">Sales - BO</Typography>
            </Box>
          </Box>
        </Box>

        {/* Tabs & Filters */}
        <Paper sx={{ mx: 3, px: 2, pt: 2, pb: 3, borderRadius: 2, bgcolor: '#F5F7FA' }} elevation={0}>
          <Box display="flex" alignItems="center" flexWrap="wrap">
            <Tabs
              value={tabValue}
              onChange={(e, v) => setTabValue(v)}
              TabIndicatorProps={{ sx: { bgcolor: '#006BB3', height: 4, borderRadius: 4, bottom: 0 } }}
              sx={{ mb: 2, flex: 1 }}
            >
              {['Mes Commandes','Brouillons','Annulées','Bloquées'].map((label,i) => (
                <Tab
                  key={label}
                  label={label}
                  sx={{
                    textTransform: 'none',
                    fontWeight: tabValue===i?600:500,
                    color: tabValue===i? '#006BB3':'#7D7D7D'
                  }}
                />
              ))}
            </Tabs>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              sx={{ ml: 2, borderRadius: 20, height: 40, textTransform: 'none' }}
            >
              Créer une nouvelle commande
            </Button>
            <FormControl size="small" sx={{ ml: 2, minWidth: 140 }}>
              <Select
                displayEmpty
                value={selectedClientId}
                onChange={e => setSelectedClientId(e.target.value)}
                sx={{ height: 40, bgcolor: '#fff', borderRadius: 2 }}
                renderValue={v => v ? clients.find(c => c.id===v)?.name : 'Client'}
              >
                <MenuItem value="">Client</MenuItem>
                {clients.map(c => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
              </Select>
            </FormControl>
            {/* ...other filters (Expédition, Paiement, Statut, Montant, Date) */}
            <TextField
              type="date"
              size="small"
              sx={{ ml: 2, width: 140, height: 40, bgcolor: '#fff', borderRadius: 2 }}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <CalendarTodayIcon />
                  </InputAdornment>
                )
              }}
            />
            <IconButton sx={{ ml: 'auto', bgcolor: '#fff', height: 40, width: 40 }}>
              <RefreshIcon color="primary" />
            </IconButton>
          </Box>
        </Paper>

        {/* DataGrid */}
        <Box flex={1} px={3} pb={3}>
          <Paper elevation={1} sx={{ borderRadius: 3, height: '100%' }}>
            <DataGrid
              rows={orders}
              columns={columns}
              pagination
              paginationMode="server"
              paginationModel={paginationModel}
              onPaginationModelChange={model => setPaginationModel(model)}
              rowCount={totalRows}
              loading={loading}
              pageSizeOptions={[10,25,50,100]}
              headerHeight={56}
              rowHeight={52}
              sx={{
                '& .MuiDataGrid-columnHeaders': {
                  bgcolor: '#E8EAED',
                  color: '#7D7D7D',
                  fontWeight: 600,
                  borderBottom: 'none',
                  textTransform: 'uppercase'
                },
                '& .MuiDataGrid-row:hover': {
                  bgcolor: 'rgba(0,107,179,0.04)'
                }
              }}
            />
          </Paper>
        </Box>
      </Box>
    </Box>
  );
}
