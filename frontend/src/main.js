// Import global styles
import './assets/main.css'
// Import Bootstrap CSS framework for responsive design
import 'bootstrap/dist/css/bootstrap.min.css'
// Import Bootstrap Icons for UI elements
import 'bootstrap-icons/font/bootstrap-icons.css'
// Import Bootstrap JavaScript for interactive components (dropdowns, modals, etc.)
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Create Vue application instance
const app = createApp(App)
// Create Pinia store for state management
const pinia = createPinia()

// Register Pinia for global state management
app.use(pinia)
// Register Vue Router for navigation
app.use(router)

// Mount the application to the DOM element with id="app"
app.mount('#app')
