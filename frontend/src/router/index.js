import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

// Create router instance with HTML5 history mode
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Public Routes
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      // Lazy-loaded route: generates separate chunk for code-splitting
      component: () => import('../views/AboutView.vue'),
    },
    // Authentication Routes (guest-only)
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: {
        title: 'Register',
        description: 'Create a new account',
        guestOnly: true, // Only accessible when not logged in
      },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: {
        title: 'Login',
        description: 'Sign in to your account',
        guestOnly: true, // Only accessible when not logged in
      },
    },
    // Protected User Routes
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Profile.vue'),
      meta: {
        title: 'Profile',
        description: 'View and edit your profile',
        requiresAuth: true, // Requires authentication
      },
    },
    // Investment Routes (public access)
    {
      path: '/investments',
      name: 'investments',
      component: () => import('../views/InvestmentList.vue'),
      meta: {
        title: 'Investment Products',
        description: 'Browse available investment products',
      },
    },
    // Admin Routes (requires admin role)
    {
      path: '/admin/investments',
      name: 'manage-investments',
      component: () => import('../views/ManageInvestments.vue'),
      meta: {
        title: 'Manage Investments',
        description: 'Admin panel for managing investment products',
        requiresAdmin: true, // Requires admin privileges
      },
    },
    {
      path: "/portfolio",
      name: "portfolio",
      component: () => import("../views/MyPortfolio.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: '/transactions',
      name: 'transactions',
      component: () => import('../views/TransactionHistory.vue'),
      meta: {
        title: 'Transactions',
        description: 'View your transaction history',
        requiresAuth: true
      }
    },
    {
      path: "/portfolio/buy",
      name: "buy-investment",
      component: () => import("../views/BuyInvestment.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/portfolio/sell",
      name: "sell-investment",
      component: () => import("../views/SellInvestment.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/buy",
      name: "BuyInvestment",
      component: () => import("@/views/BuyInvestment.vue")
    },
    {
      path: "/sell",
      name: "SellInvestment",
      component: () => import("@/views/SellInvestment.vue")
    },

    {
      path: '/admin/users',
      name: 'user-list',
      component: () => import('../views/UserList.vue'),
      meta: {
        title: 'User Management',
        description: 'Admin panel for managing users',
        requiresAdmin: true, // Requires admin privileges
      },
    },
// Ticket creation and helpcenter paths 
    {
      path: '/help-center',
      name: 'help-center',
      component: () => import('../views/HelpCenter.vue'),
      meta: {
        title: 'Help Center',
        requiresAuth: true,
      },
    },
    {
      path: '/help-center/new',
      name: 'create-ticket',
      component: () => import('../views/CreateTicket.vue'),
      meta: {
        title: 'Create Ticket',
        requiresAuth: true,
      },
    },
    {
      path: '/help-center/my-tickets',
      name: 'user-ticket-list',
      component: () => import('../views/UserTicketList.vue'),
      meta: {
        title: 'My Tickets',
        requiresAuth: true,
      },
    },
    {
      path: '/admin/tickets',
      name: 'admin-ticket-list',
      component: () => import('../views/AdminTicketList.vue'),
      meta: {
        title: 'Manage Tickets',
        requiresAdmin: true,
      },
    },
    {
      path: '/ticket/:id',
      name: 'ticket-detail',
      component: () => import('../views/TicketDetail.vue'),
      meta: {
        title: 'Ticket Details',
        requiresAuth: true,
      },
      props: true, // This allows the :id to be passed as a prop
    },
  ],
})

// Check if user has valid authentication token
function isAuthenticated() {
  const currentUser = localStorage.getItem('currentUser')
  return !!currentUser
}

// Check if authenticated user has admin role
function isAdmin() {
  const currentUser = localStorage.getItem('currentUser')
  if (!currentUser) return false
  const user = JSON.parse(currentUser)
  return user.role === 'ADMIN'
}

// Global navigation guard - runs before each route change
router.beforeEach((to, from, next) => {
  // Update page title based on route metadata
  if (to.meta.title) {
    document.title = `${to.meta.title} - Investment Tracker`
  } else {
    document.title = 'Investment & Portfolio Tracker'
  }

  // Redirect to login if route requires authentication
  if (to.meta.requiresAuth && !isAuthenticated()) {
    console.warn('⚠️ Access denied: Authentication required')
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  // Redirect to home if route requires admin but user is not admin
  if (to.meta.requiresAdmin && !isAdmin()) {
    console.warn('⚠️ Access denied: Admin privileges required')
    alert('Access denied! This page is only accessible to administrators.')
    next({ name: 'home' })
    return
  }

  // Redirect authenticated users away from guest-only pages (login/register)
  if (to.meta.guestOnly && isAuthenticated()) {
    console.log('Already authenticated, redirecting to home')
    next({ name: 'home' })
    return
  }

  // Allow navigation to proceed
  next()
})

export default router
