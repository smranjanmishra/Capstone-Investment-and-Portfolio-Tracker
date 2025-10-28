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

//Import vue-echarts and echarts core setup
import ECharts from 'vue-echarts'
import { use } from 'echarts/core'

// Import only the modules you need
import {
  CanvasRenderer
} from 'echarts/renderers'
import {
  PieChart,
  BarChart,
  LineChart
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent
} from 'echarts/components'

//Register ECharts modules
use([
  CanvasRenderer,
  PieChart,
  BarChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
])

// Create Vue application instance
const app = createApp(App)
// Create Pinia store for state management
const pinia = createPinia()
// Register Pinia for global state management
app.use(pinia)
// Register Vue Router for navigation
app.use(router)

//Register the ECharts component globally
app.component('v-chart', ECharts)

// Mount the application to the DOM element with id="app"
app.mount('#app')
