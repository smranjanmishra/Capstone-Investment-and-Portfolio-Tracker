<template>
  <div class="login-container">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
          <!-- Login Card -->
          <div class="card shadow-lg border-0">
            <div class="card-body p-5">
              <!-- Header -->
              <div class="text-center mb-4">
                <div class="logo-circle mx-auto mb-3">
                  <i class="bi bi-person-circle"></i>
                </div>
                <h2 class="fw-bold">Welcome Back</h2>
                <p class="text-muted">Sign in to your account</p>
              </div>

              <!-- Error Message -->
              <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                {{ errorMessage }}
                <button type="button" class="btn-close" @click="errorMessage = ''"></button>
              </div>

              <!-- Login Form -->
              <form @submit.prevent="handleLogin">
                <!-- Email -->
                <div class="mb-3">
                  <label for="email" class="form-label">Email Address</label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                    <input
                      id="email"
                      v-model="formData.email"
                      type="email"
                      class="form-control"
                      placeholder="Enter your email"
                      required
                    />
                  </div>
                </div>

                <!-- Password -->
                <div class="mb-3">
                  <label for="password" class="form-label">Password</label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                    <input
                      id="password"
                      v-model="formData.password"
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control"
                      placeholder="Enter your password"
                      required
                    />
                    <button
                      class="btn btn-outline-secondary"
                      type="button"
                      @click="showPassword = !showPassword"
                    >
                      <i :class="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                    </button>
                  </div>
                </div>

                <!-- Remember Me & Forgot Password -->
                <!-- <div class="d-flex justify-content-between align-items-center mb-4">
                  <div class="form-check">
                    <input
                      id="remember"
                      v-model="formData.rememberMe"
                      class="form-check-input"
                      type="checkbox"
                    />
                    <label class="form-check-label" for="remember">
                      Remember me
                    </label>
                  </div>
                  <a href="#" class="text-decoration-none small">Forgot Password?</a>
                </div> -->

                <!-- Submit Button -->
                <button
                  type="submit"
                  class="btn btn-primary w-100 py-2 fw-bold"
                  :disabled="loading"
                >
                  <span v-if="loading">
                    <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Signing In...
                  </span>
                  <span v-else>
                    <i class="bi bi-box-arrow-in-right me-2"></i>
                    Sign In
                  </span>
                </button>
              </form>

              <!-- Demo Accounts -->
              <!-- <div class="mt-4">
                <p class="text-center text-muted small mb-2">Quick Login (Demo)</p>
                <div class="d-grid gap-2">
                  <button
                    class="btn btn-outline-secondary btn-sm"
                    @click="loginAsUser"
                    :disabled="loading"
                  >
                    <i class="bi bi-person me-1"></i> Login as User
                  </button>
                  <button
                    class="btn btn-outline-dark btn-sm"
                    @click="loginAsAdmin"
                    :disabled="loading"
                  >
                    <i class="bi bi-shield-check me-1"></i> Login as Admin
                  </button>
                </div>
              </div> -->

              <!-- Register Link -->
              <div class="text-center mt-4">
                <p class="text-muted mb-0">
                  Don't have an account?
                  <RouterLink to="/register" class="text-decoration-none fw-bold">
                    Sign Up
                  </RouterLink>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/services/api'

export default {
  name: 'UserLogin',

  setup() {
    const router = useRouter()

    // Form Data
    const formData = reactive({
      email: '',
      password: '',
    })

    // State
    const loading = ref(false)
    const errorMessage = ref('')
    const showPassword = ref(false)

    // Handle login
    async function handleLogin() {
      errorMessage.value = ''
      loading.value = true

      try {
        // Call backend login API directly
        const loginResponse = await apiClient.post('/auth/login', {
          email: formData.email,
          password: formData.password
        })

        // Store JWT token
        const token = loginResponse.data.token
        localStorage.setItem('authToken', token)
        console.log(' Login successful, token stored')

        // Fetch user profile
        const profileResponse = await apiClient.get('/user/profile')
        const userProfile = profileResponse.data
        
        // Store user data in localStorage
        localStorage.setItem('currentUser', JSON.stringify({
          ...userProfile,
          fullName: userProfile.name // Map backend 'name' to frontend 'fullName'
        }))
        console.log(' User profile loaded:', userProfile.email)

        // Dispatch custom event for App.vue to update navbar
        window.dispatchEvent(new CustomEvent('user-logged-in'))

        // Redirect based on role
        if (userProfile.role === 'ADMIN') {
          router.push('/admin/investments')
        } else {
          router.push('/investments')
        }
      } catch (error) {
        console.error('Login error:', error)
        
        if (error.response?.status === 401) {
          errorMessage.value = 'Invalid email or password'
        } else if (error.response?.status === 400) {
          errorMessage.value = 'Please provide valid email and password'
        } else {
          errorMessage.value = error.message || 'Login failed. Please try again.'
        }
      } finally {
        loading.value = false
      }
    }

    // Quick login as demo user (for testing)
    // Note: User must be registered in backend database first
    async function loginAsUser() {
      formData.email = 'user@example.com'
      formData.password = 'User@123'
      await handleLogin()
    }

    // Quick login as demo admin (for testing)
    // Note: Admin user must be registered in backend database first
    async function loginAsAdmin() {
      formData.email = 'admin@example.com'
      formData.password = 'Admin@123'
      await handleLogin()
    }

    return {
      formData,
      loading,
      errorMessage,
      showPassword,
      handleLogin,
      loginAsUser,
      loginAsAdmin,
    }
  },
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 3rem 0;
}

.card {
  border-radius: 1rem;
  border: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logo-circle {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2.5rem;
}

.input-group-text {
  background-color: #f8f9fa;
  border-right: none;
  color: #6c757d;
}

.form-control {
  border-left: none;
}

.form-control:focus {
  border-color: #0d6efd;
  box-shadow: none;
  border-left: none;
}

.input-group:focus-within .input-group-text {
  border-color: #0d6efd;
}

.btn-primary {
  background: #0d6efd;
  border: none;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background: #0b5ed7;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(13, 110, 253, 0.3);
}

.btn-outline-secondary {
  border-left: none;
  background: transparent;
}

.btn-outline-secondary:hover {
  background: #e9ecef;
}

.btn-outline-secondary.btn-sm,
.btn-outline-dark.btn-sm {
  padding: 0.5rem 1rem;
  font-weight: 500;
}

.btn-outline-dark:hover {
  background: #212529;
  color: white;
}

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

a {
  color: #0d6efd;
  text-decoration: none;
}

a:hover {
  color: #0b5ed7;
  text-decoration: underline;
}

.alert {
  border-radius: 0.5rem;
  border: none;
}
</style>
