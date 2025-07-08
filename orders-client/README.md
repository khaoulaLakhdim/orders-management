# Orders Client

React frontend for the Orders Management System.

## Features

- **Login Page**: Beautiful two-column layout with authentication
- **App Shell**: Permanent drawer with navigation menu
- **Orders Page**: DataGrid with server-side pagination and filtering
- **Material UI**: Modern UI components with custom theme
- **Responsive Design**: Works on desktop and mobile

## Tech Stack

- React 18+
- Material UI v5
- MUI DataGrid
- Axios for API calls
- React Router for navigation
- Vite for build tooling

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```

3. Open [http://localhost:5173](http://localhost:5173) in your browser

## Environment Variables

Create a `.env` file in the root directory:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

## Project Structure

```
src/
├── api/           # API service layer
├── components/    # Reusable components
├── pages/         # Page components
├── theme/         # Material UI theme
└── App.jsx        # Main app component
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
