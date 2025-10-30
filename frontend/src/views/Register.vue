<template>
  <div class="register-container">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <!-- Registration Card -->
          <div class="card shadow-lg border-0">
            <div class="card-body p-5">
              <!-- Header -->
              <div class="text-center mb-4">
                <div class="logo-circle mx-auto mb-3">
                  <i class="bi bi-person-plus-fill"></i>
                </div>
                <h2 class="fw-bold">Create Account</h2>
                <p class="text-muted">Join us to start investing today</p>
              </div>

              <!-- Success Message -->
              <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                {{ successMessage }}
                <button type="button" class="btn-close" @click="successMessage = ''"></button>
              </div>

              <!-- Error Message -->
              <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                {{ errorMessage }}
                <button type="button" class="btn-close" @click="errorMessage = ''"></button>
              </div>

              <!-- Registration Form -->
              <form @submit.prevent="handleRegister">
                <!-- Full Name -->
                <div class="mb-3">
                  <label for="fullName" class="form-label">
                    Full Name <span class="text-danger">*</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-person"></i></span>
                    <input
                      id="fullName"
                      v-model="formData.fullName"
                      type="text"
                      class="form-control"
                      :class="{ 'is-invalid': errors.fullName }"
                      placeholder="Enter your full name"
                      required
                    />
                  </div>
                  <div v-if="errors.fullName" class="invalid-feedback d-block">
                    {{ errors.fullName }}
                  </div>
                </div>

                <!-- Email -->
                <div class="mb-3">
                  <label for="email" class="form-label">
                    Email Address <span class="text-danger">*</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                    <input
                      id="email"
                      v-model="formData.email"
                      type="email"
                      class="form-control"
                      :class="{ 'is-invalid': errors.email }"
                      placeholder="Enter your email"
                      required
                    />
                  </div>
                  <div v-if="errors.email" class="invalid-feedback d-block">
                    {{ errors.email }}
                  </div>
                </div>

                <!-- Phone Number -->
                <div class="mb-3">
                  <label for="phone" class="form-label">
                    Phone Number <span class="text-muted">(Optional)</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-telephone"></i></span>
                    <input
                      id="phone"
                      v-model="formData.phone"
                      type="tel"
                      class="form-control"
                      :class="{ 'is-invalid': errors.phone }"
                      placeholder="Enter your phone number"
                    />
                  </div>
                  <div v-if="errors.phone" class="invalid-feedback d-block">
                    {{ errors.phone }}
                  </div>
                </div>

                <!-- Password -->
                <div class="mb-3">
                  <label for="password" class="form-label">
                    Password <span class="text-danger">*</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                    <input
                      id="password"
                      v-model="formData.password"
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control"
                      :class="{ 'is-invalid': errors.password }"
                      placeholder="Create a strong password"
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
                  <div v-if="errors.password" class="invalid-feedback d-block">
                    {{ errors.password }}
                  </div>
                  <small class="text-muted">
                    At least 8 characters with uppercase, lowercase, number & special character
                  </small>
                </div>

                <!-- Confirm Password -->
                <div class="mb-3">
                  <label for="confirmPassword" class="form-label">
                    Confirm Password <span class="text-danger">*</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                    <input
                      id="confirmPassword"
                      v-model="formData.confirmPassword"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      class="form-control"
                      :class="{ 'is-invalid': errors.confirmPassword }"
                      placeholder="Confirm your password"
                      required
                    />
                    <button
                      class="btn btn-outline-secondary"
                      type="button"
                      @click="showConfirmPassword = !showConfirmPassword"
                    >
                      <i :class="showConfirmPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                    </button>
                  </div>
                  <div v-if="errors.confirmPassword" class="invalid-feedback d-block">
                    {{ errors.confirmPassword }}
                  </div>
                </div>

                <!-- Submit Button -->
                <button
                  type="submit"
                  class="btn btn-primary w-100 py-2 fw-bold"
                  :disabled="loading || !formData.fullName || !formData.email || !formData.password || !formData.confirmPassword"
                >
                  <span v-if="loading">
                    <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Creating Account...
                  </span>
                  <span v-else>
                    <i class="bi bi-person-check me-2"></i>
                    Create Account
                  </span>
                </button>
              </form>

              <!-- Login Link -->
              <div class="text-center mt-4">
                <p class="text-muted mb-0">
                  Already have an account?
                  <RouterLink to="/login" class="text-decoration-none fw-bold">
                    Sign In
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
  name: 'UserRegister',

  setup() {
    const router = useRouter()

    // Form Data
    const formData = reactive({
      fullName: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
    })

    // State
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const errors = ref({})
    const showPassword = ref(false)
    const showConfirmPassword = ref(false)

    // Validate form data
    function validateForm() {
      errors.value = {}

      // Full Name validation
      if (!formData.fullName.trim()) {
        errors.value.fullName = 'Full name is required'
      } else if (formData.fullName.trim().length < 3) {
        errors.value.fullName = 'Full name must be at least 3 characters'
      }

      // Email validation
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!formData.email.trim()) {
        errors.value.email = 'Email is required'
      } else if (!emailRegex.test(formData.email)) {
        errors.value.email = 'Please enter a valid email address'
      }

      // Phone validation (optional)
      if (formData.phone.trim()) {
        const phoneRegex = /^[0-9]{10}$/
        if (!phoneRegex.test(formData.phone.replace(/[-\s]/g, ''))) {
          errors.value.phone = 'Please enter a valid 10-digit phone number'
        }
      }

      // Password validation
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
      if (!formData.password) {
        errors.value.password = 'Password is required'
      } else if (!passwordRegex.test(formData.password)) {
        errors.value.password =
          'Password must be at least 8 characters with uppercase, lowercase, number & special character'
      }

      // Confirm Password validation
      if (!formData.confirmPassword) {
        errors.value.confirmPassword = 'Please confirm your password'
      } else if (formData.password !== formData.confirmPassword) {
        errors.value.confirmPassword = 'Passwords do not match'
      }

      return Object.keys(errors.value).length === 0
    }

    // Handle registration
    async function handleRegister() {
      errorMessage.value = ''
      successMessage.value = ''
      const normalizedEmail = formData.email.trim().toLowerCase()

      if (!validateForm()) {
        errorMessage.value = 'Please fix the errors and try again'
        return
      }

      loading.value = true

      try {
        // Call backend register API directly
        const requestData = {
          name: formData.fullName,
          email: normalizedEmail,
          password: formData.password
        }

        // Only include phone if provided
        if (formData.phone.trim()) {
          requestData.phone = formData.phone
        }

        const response = await apiClient.post('/auth/register', requestData)

        // Registration successful
        console.log(' Registration successful:', response.data.email)

        successMessage.value = 'Account created successfully! Redirecting to login...'

        // Redirect to login after 2 seconds
        setTimeout(() => {
          router.push('/login')
        }, 2000)
      } catch (error) {
        console.error('Registration error:', error)

        if (error.response?.status === 409 || error.response?.status === 400) {
          const errorMsg = error.response?.data?.message || error.response?.data?.error
          errorMessage.value = errorMsg || 'Email already registered or invalid data'
        } else {
          errorMessage.value = error.message || 'Registration failed. Please try again.'
        }
      } finally {
        loading.value = false
      }
    }

    return {
      formData,
      loading,
      successMessage,
      errorMessage,
      errors,
      showPassword,
      showConfirmPassword,
      handleRegister,
    }
  },
}
</script>

<style scoped>
.register-container {
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
  font-size: 2rem;
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

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.form-check-input:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  font-size: 0.875rem;
  margin-top: 0.25rem;
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

small.text-muted {
  font-size: 0.8rem;
  color: #6c757d;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
