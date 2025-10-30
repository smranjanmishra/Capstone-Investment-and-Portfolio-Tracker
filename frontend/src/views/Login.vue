<template>
  <div class="login-container">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
          <div class="card shadow-lg border-0">
            <div class="card-body p-5">
              <div class="text-center mb-4">
                <div class="logo-circle mx-auto mb-3">
                  <i class="bi bi-person-circle"></i>
                </div>
                <h2 class="fw-bold">Welcome Back</h2>
                <p class="text-muted">Sign in to your account</p>
              </div>

              <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                {{ errorMessage }}
                <button type="button" class="btn-close" @click="errorMessage = ''"></button>
              </div>

              <form @submit.prevent.stop="handleLogin">
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

                <button
                  type="submit"
                  class="btn btn-primary w-100 py-2 fw-bold"
                  :disabled="loading || !formData.email || !formData.password"
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
    const formData = reactive({
      email: '',
      password: '',
    })
    const loading = ref(false)
    const errorMessage = ref('')
    const showPassword = ref(false)

    async function handleLogin() {
      errorMessage.value = ''
      loading.value = true
      try {
        formData.email = formData.email.toLowerCase().trim()
        const loginResponse = await apiClient.post('/auth/login', {
          email: formData.email,
          password: formData.password
        })
        const token = loginResponse.data.token
        localStorage.setItem('authToken', token)
        const profileResponse = await apiClient.get('/user/profile')
        const userProfile = profileResponse.data
        localStorage.setItem('currentUser', JSON.stringify({
          ...userProfile,
          fullName: userProfile.name
        }))
        window.dispatchEvent(new CustomEvent('user-logged-in'))
        if (userProfile.role === 'ADMIN') {
          router.push('/admin/investments')
        } else {
          router.push('/investments')
        }
      } catch (error) {
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

    async function loginAsUser() {
      formData.email = 'user@example.com'
      formData.password = 'User@123'
      await handleLogin()
    }

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

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
