<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { ref, onMounted } from 'vue'

// Currently logged-in user (null if not authenticated)
const currentUser = ref(null)

// Load current user data from localStorage
function loadCurrentUser() {
  const user = localStorage.getItem('currentUser')
  if (user) {
    currentUser.value = JSON.parse(user)
    console.log(' Current user loaded:', currentUser.value)
  } else {
    currentUser.value = null
    console.log('No user logged in')
  }
}

// Handle user logout - clear session and redirect to home
function logout() {
  if (confirm('Are you sure you want to logout?')) {
    // Clear authentication data from localStorage
    localStorage.removeItem('currentUser')
    localStorage.removeItem('authToken')
    currentUser.value = null

    // Dispatch custom event for other components to react
    window.dispatchEvent(new CustomEvent('user-logged-out'))

    // Redirect to home page
    window.location.href = '/'
  }
}

onMounted(() => {
  // Load user data on app initialization
  loadCurrentUser()

  // Listen for storage changes (cross-tab synchronization)
  window.addEventListener('storage', loadCurrentUser)

  // Listen for custom login event (same-tab updates after login)
  window.addEventListener('user-logged-in', loadCurrentUser)

  // Listen for custom logout event (same-tab updates after logout)
  window.addEventListener('user-logged-out', () => {
    currentUser.value = null
  })
})
</script>

<template>
  <div id="app">
    <!-- Navigation Header -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
      <div class="container-fluid">
        <RouterLink to="/" class="navbar-brand">
          <i class="bi bi-graph-up-arrow me-2"></i>
          Investment Tracker
        </RouterLink>

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <RouterLink to="/" class="nav-link">
                <i class="bi bi-house-door-fill me-1"></i>
                Home
              </RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/investments" class="nav-link">
                <i class="bi bi-bar-chart-line-fill me-1"></i>
                Investments
              </RouterLink>
            </li>
            <li class="nav-item" v-if="currentUser">
              <RouterLink to="/portfolio" class="nav-link">
                <i class="bi bi-wallet2 me-1"></i>
                Portfolio
              </RouterLink>
            </li>


            <li class="nav-item" v-if="currentUser && currentUser.role === 'ADMIN'">
              <RouterLink to="/admin/investments" class="nav-link">
                <i class="bi bi-gear-fill me-1"></i>
                Manage Products
              </RouterLink>
            </li>
            <li class="nav-item" v-if="currentUser && currentUser.role === 'ADMIN'">
              <RouterLink to="/admin/users" class="nav-link">
                <i class="bi bi-people-fill me-1"></i>
                Manage Users
              </RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/about" class="nav-link">
                <i class="bi bi-info-circle-fill me-1"></i>
                About
              </RouterLink>
            </li>
            <li class="nav-item" v-if="currentUser">
              <RouterLink to="/help-center" class="nav-link">
                <i class="bi bi-question-circle-fill me-1"></i>
                Help
              </RouterLink>
            </li>
          </ul>

          <ul class="navbar-nav">
            <!-- If user is not logged in -->
            <li class="nav-item" v-if="!currentUser">
              <RouterLink to="/login" class="nav-link">
                <i class="bi bi-box-arrow-in-right me-1"></i>
                Login
              </RouterLink>
            </li>
            <li class="nav-item" v-if="!currentUser">
              <RouterLink to="/register" class="nav-link btn-register">
                <i class="bi bi-person-plus-fill me-1"></i>
                Register
              </RouterLink>
            </li>

            <!-- If user is logged in -->
            <li class="nav-item dropdown" v-if="currentUser">
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="userDropdown"
                role="button"
                data-bs-toggle="dropdown"
              >
                <i class="bi bi-person-circle me-1"></i>
                {{ currentUser.fullName }}
                <!-- <span 
                  class="badge ms-2"
                  :class="currentUser.role === 'ADMIN' ? 'bg-danger' : 'bg-light text-dark'"
                >
                  {{ currentUser.role }}
                </span> -->
              </a>
              <ul class="dropdown-menu dropdown-menu-end">
                <li>
                  <RouterLink to="/profile" class="dropdown-item">
                    <i class="bi bi-person-fill me-2"></i>
                    Profile
                  </RouterLink>
                </li>
                <li>
                  <RouterLink to="/transactions" class="dropdown-item">
                    <i class="bi bi-clock-history me-2"></i>
                    History
                  </RouterLink>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li>
                  <a class="dropdown-item text-danger" href="#" @click.prevent="logout">
                    <i class="bi bi-box-arrow-right me-2"></i>
                    Logout
                  </a>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="main-content">
      <RouterView />
    </main>

    <!-- Footer -->
    <footer class="footer bg-dark text-white mt-5">
      <div class="container text-center py-4">
        <p class="mb-0">
          <i class="bi bi-c-circle me-1"></i>
          2025 Investment & Portfolio Tracker. All rights reserved.
        </p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  padding: 0.75rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: 700;
  color: white !important;
  transition: transform 0.2s ease;
}

.navbar-brand:hover {
  transform: scale(1.05);
}

.nav-link {
  color: rgba(255, 255, 255, 0.85) !important;
  font-weight: 500;
  padding: 0.5rem 1rem !important;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: white !important;
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.nav-link.router-link-active {
  color: white !important;
  background-color: rgba(255, 255, 255, 0.2);
  font-weight: 600;
}

.btn-register {
  background-color: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
}

.btn-register:hover {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dropdown-item {
  padding: 0.5rem 1rem;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background-color: rgba(13, 110, 253, 0.1);
}

.dropdown-item.text-danger:hover {
  background-color: rgba(220, 53, 69, 0.1);
}

.badge {
  font-size: 0.7rem;
  padding: 0.25rem 0.5rem;
}

.main-content {
  flex: 1;
  background: #f8f9fa;
  padding: 0;
  margin: 0;
  width: 100%;
}

.footer {
  margin-top: auto;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1);
}

/* Responsive adjustments */
@media (max-width: 991px) {
  .navbar-nav {
    margin-top: 1rem;
  }

  .nav-link {
    margin: 0.25rem 0;
  }
}
</style>
