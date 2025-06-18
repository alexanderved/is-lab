import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { CustomProvider } from 'rsuite';
import './index.css'
import App from './App.tsx'
import 'rsuite/dist/rsuite.min.css';
import { BrowserRouter } from 'react-router-dom';

createRoot(document.getElementById('root')!).render(
  	<StrictMode>
    	<CustomProvider theme="light">
			<BrowserRouter>
				<App />
			</BrowserRouter>
	    </CustomProvider>
  	</StrictMode>,
)
