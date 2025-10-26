<template>
  <div class="profile-container">
    <div class="container py-5">
      <!-- Page Header -->
      <div class="page-header mb-4">
        <h1 class="page-title">
          <i class="bi bi-person-circle me-2"></i>
          My Profile
        </h1>
        <p class="page-subtitle">View and manage your account information</p>
      </div>

      <!-- Not Logged In State -->
      <div v-if="!currentUser" class="alert alert-warning">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        You need to <RouterLink to="/login" class="alert-link">login</RouterLink> to view your profile.
      </div>

      <!-- Profile Content -->
      <div v-else class="row">
        <!-- Profile Card -->
        <div class="col-lg-4 mb-4">
          <div class="card shadow-sm">
            <div class="card-body text-center p-4">
              <!-- Avatar -->
              <div class="avatar-circle mx-auto mb-3">
                <i class="bi bi-person-fill"></i>
              </div>

              <!-- Name & Role -->
              <h3 class="fw-bold mb-1">{{ currentUser.fullName }}</h3>
              <span class="badge" :class="currentUser.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'">
                <i class="bi" :class="currentUser.role === 'ADMIN' ? 'bi-shield-check' : 'bi-person'"></i>
                {{ currentUser.role }}
              </span>

              <!-- Stats -->
              <!-- <div class="profile-stats mt-4">
                <div class="stat-item">
                  <div class="stat-value">{{ formatDate(currentUser.loggedInAt) }}</div>
                  <div class="stat-label">Last Login</div>
                </div>
              </div> -->

              <!-- Actions -->
              <!-- <div class="d-grid gap-2 mt-4">
                <button class="btn btn-outline-primary" @click="showEditModal = true">
                  <i class="bi bi-pencil me-2"></i>
                  Edit Profile
                </button>
                <button class="btn btn-outline-danger" @click="handleLogout">
                  <i class="bi bi-box-arrow-right me-2"></i>
                  Logout
                </button>
              </div> -->
            </div>
          </div>
        </div>

        <!-- Profile Details -->
        <div class="col-lg-8">
          <!-- Personal Information -->
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-white">
              <h5 class="mb-0">
                <i class="bi bi-person-badge me-2"></i>
                Personal Information
              </h5>
            </div>
            <div class="card-body">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="text-muted small">Full Name</label>
                  <p class="fw-bold mb-0">{{ currentUser.fullName }}</p>
                </div>
                <div class="col-md-6">
                  <label class="text-muted small">Email Address</label>
                  <p class="fw-bold mb-0">{{ currentUser.email }}</p>
                </div>
                <div class="col-md-6">
                  <label class="text-muted small">Phone Number</label>
                  <p class="fw-bold mb-0">{{ currentUser.phone || 'Not provided' }}</p>
                </div>
                <div class="col-md-6">
                  <label class="text-muted small">Account Role</label>
                  <p class="fw-bold mb-0">{{ currentUser.role }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Account Activity -->
          <!-- <div class="card shadow-sm">
            <div class="card-header bg-white">
              <h5 class="mb-0">
                <i class="bi bi-clock-history me-2"></i>
                Account Activity
              </h5>
            </div>
            <div class="card-body">
              <div class="activity-list">
                <div class="activity-item">
                  <div class="activity-icon bg-success">
                    <i class="bi bi-box-arrow-in-right"></i>
                  </div>
                  <div class="activity-content">
                    <p class="mb-1 fw-bold">Logged In</p>
                    <small class="text-muted">{{ formatDate(currentUser.loggedInAt) }}</small>
                  </div>
                </div>
                <div class="activity-item">
                  <div class="activity-icon bg-primary">
                    <i class="bi bi-person-check"></i>
                  </div>
                  <div class="activity-content">
                    <p class="mb-1 fw-bold">Account Created</p>
                    <small class="text-muted">{{ getAccountAge() }}</small>
                  </div>
                </div>
              </div>
            </div> -->
          <!-- </div> -->
        </div>
      </div>
    </div>

    <!-- Edit Profile Modal
    <div v-if="showEditModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-pencil me-2"></i>
              Edit Profile
            </h5>
            <button type="button" class="btn-close" @click="closeEditModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleUpdateProfile">
              <div class="mb-3">
                <label for="editFullName" class="form-label">Full Name</label>
                <input
                  id="editFullName"
                  v-model="editFormData.fullName"
                  type="text"
                  class="form-control"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="editPhone" class="form-label">Phone Number</label>
                <input
                  id="editPhone"
                  v-model="editFormData.phone"
                  type="tel"
                  class="form-control"
                />
              </div>
              <div class="alert alert-info">
                <small>
                  <i class="bi bi-info-circle me-1"></i>
                  Email address cannot be changed for security reasons.
                </small>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditModal">Cancel</button>
            <button type="button" class="btn btn-primary" @click="handleUpdateProfile">
              <i class="bi bi-check-circle me-2"></i>
              Save Changes
            </button>
          </div>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/services/api'

export default {
  name: 'UserProfile',

  setup() {
    const router = useRouter()

    // State
    const currentUser = ref(null)
    const showEditModal = ref(false)
    const loading = ref(false)
    const editFormData = reactive({
      fullName: '',
      phone: '',
    })

    // Check if user is authenticated
    function isAuthenticated() {
      const token = localStorage.getItem('authToken')
      return !!token
    }

    // Load current user from backend
    async function loadCurrentUser() {
      // First check if user is authenticated
      if (!isAuthenticated()) {
        currentUser.value = null
        return
      }

      try {
        loading.value = true
        
        // Fetch user profile from backend
        const response = await apiClient.get('/user/profile')
        const userProfile = response.data
        
        currentUser.value = {
          ...userProfile,
          fullName: userProfile.name // Backend uses 'name', frontend uses 'fullName'
        }
        
        editFormData.fullName = currentUser.value.fullName
        editFormData.phone = currentUser.value.phone || ''
        
        console.log(' User profile loaded from backend')
      } catch (error) {
        console.error('Failed to load user profile:', error)
        
        // If session expired or unauthorized, redirect to login
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken')
          localStorage.removeItem('currentUser')
          router.push('/login')
        }
      } finally {
        loading.value = false
      }
    }

    // Format date
    function formatDate(dateString) {
      if (!dateString) return 'N/A'
      const date = new Date(dateString)
      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    }

    // Handle logout
    function handleLogout() {
      if (confirm('Are you sure you want to logout?')) {
        localStorage.removeItem('authToken')
        localStorage.removeItem('currentUser')
        console.log(' Logged out successfully')
        router.push('/login')
      }
    }

    // Close edit modal
    function closeEditModal() {
      showEditModal.value = false
      // Reset form data
      editFormData.fullName = currentUser.value.fullName
      editFormData.phone = currentUser.value.phone || ''
    }

    onMounted(() => {
      loadCurrentUser()
    })

    return {
      currentUser,
      showEditModal,
      editFormData,
      loading,
      formatDate,
      handleLogout,
      closeEditModal,
    }
  },
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 2rem 0;
}

.page-header {
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #6c757d;
  margin-bottom: 0;
}

.avatar-circle {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 4rem;
}

.profile-stats {
  border-top: 1px solid #e9ecef;
  padding-top: 1rem;
}

.stat-item {
  padding: 0.5rem 0;
}

.stat-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
}

.stat-label {
  font-size: 0.875rem;
  color: #6c757d;
}

.card-header {
  border-bottom: 2px solid #f8f9fa;
  padding: 1rem 1.25rem;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  background-color: #f8f9fa;
  border-radius: 0.5rem;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.25rem;
}

.activity-content {
  flex: 1;
}

.modal.show {
  display: block;
}
</style>
