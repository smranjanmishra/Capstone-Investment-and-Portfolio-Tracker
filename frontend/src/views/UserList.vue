<template>
  <div class="user-list-container">
    <div class="container-fluid py-4">
      <!-- Page Header -->
      <div class="page-header d-flex justify-content-between align-items-center mb-4">
        <div>
          <h1 class="page-title">
            <i class="bi bi-people-fill me-2"></i>
            User Management
          </h1>
          <p class="page-subtitle">View and manage all registered users</p>
        </div>

        <div>
          <span class="badge bg-primary fs-6">
            <i class="bi bi-person-circle me-1"></i>
            {{ users.length }} {{ users.length === 1 ? 'User' : 'Users' }}
          </span>
        </div>
      </div>

        <div class="row mt-4 justify-content-center">
          <UserStatCard
            icon="bi-people-fill"
            icon-class="bg-primary text-white"
            :value="users.length"
            label="Total Users"
          />
          <UserStatCard
            icon="bi-shield-check"
            icon-class="bg-danger text-white"
            :value="adminCount"
            label="Administrators"
          />
          <UserStatCard
            icon="bi-person"
            icon-class="bg-success text-white"
            :value="regularUserCount"
            label="Regular Users"
          />
        </div>

      <!-- Access Denied -->
      <div v-if="!isAdmin" class="alert alert-danger">
        <i class="bi bi-shield-exclamation me-2"></i>
        Access Denied! This page is only accessible to administrators.
        <RouterLink to="/login" class="alert-link ms-2">Login as Admin</RouterLink>
      </div>

      <!-- Users Table -->
      <div v-else>
        <!-- Error Alert -->
        <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2"></i>
          {{ error }}
          <button type="button" class="btn-close" @click="error = null"></button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-3 text-muted">Loading users...</p>
        </div>

        <!-- Content -->
        <div v-else>
        <!-- Filters -->
        <div class="card shadow-sm mb-4 mt-5">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-6">
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="bi bi-search"></i>
                  </span>
                  <input
                    v-model="searchQuery"
                    type="text"
                    class="form-control"
                    placeholder="Search by name or email..."
                  />
                </div>
              </div>
              <div class="col-md-3">
                <select v-model="filterRole" class="form-select">
                  <option value="">All Roles</option>
                  <option value="ADMIN">Admin</option>
                  <option value="USER">User</option>
                </select>
              </div>
              <div class="col-md-3">
                <button class="btn btn-outline-secondary w-100" @click="clearFilters">
                  <i class="bi bi-x-circle me-2"></i>
                  Clear Filters
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Users Table -->
        <div class="card shadow-sm">
          <div class="card-body p-0">
            <div v-if="filteredUsers.length === 0" class="text-center py-5">
              <i class="bi bi-inbox display-1 text-muted"></i>
              <p class="text-muted mt-3">No users found</p>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                  <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Name</th>
                    <th scope="col">Email</th>
                    <th scope="col">Phone</th>
                    <th scope="col">Role</th>
                    <th scope="col">Registered</th>
                    <th scope="col" class="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <UserTableRow
                    v-for="user in filteredUsers"
                    :key="user.id"
                    :user="user"
                    @view="viewUser"
                  />
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Statistics Cards -->

        </div>
      </div>
    </div>

    <!-- User Details Modal -->
    <UserDetailsModal
      :show="showDetailsModal"
      :user="selectedUser"
      @close="showDetailsModal = false"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import apiClient from '@/services/api'
import UserStatCard from '@/components/UserStatCard.vue'
import UserTableRow from '@/components/UserTableRow.vue'
import UserDetailsModal from '@/components/UserDetailsModal.vue'
import { useAuth } from '@/composables/useAuth'

export default {
  name: 'UserListView',

  components: {
    UserStatCard,
    UserTableRow,
    UserDetailsModal
  },

  setup() {
    const { isAdmin } = useAuth()

    // State
    const users = ref([])
    const searchQuery = ref('')
    const filterRole = ref('')
    const showDetailsModal = ref(false)
    const selectedUser = ref(null)
    const loading = ref(false)
    const error = ref(null)

    // Filtered users
    const filteredUsers = computed(() => {
      let result = [...users.value]

      // Filter by search query
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(
          (user) =>
            user.fullName.toLowerCase().includes(query) ||
            user.email.toLowerCase().includes(query)
        )
      }

      // Filter by role
      if (filterRole.value) {
        result = result.filter((user) => user.role === filterRole.value)
      }

      return result
    })

    // Statistics
    const adminCount = computed(() => {
      return users.value.filter((u) => u.role === 'ADMIN').length
    })

    const regularUserCount = computed(() => {
      return users.value.filter((u) => u.role === 'USER').length
    })

    // Load users from backend API
    async function loadUsers() {
      try {
        loading.value = true
        error.value = null

        // Fetch users from backend API
        const response = await apiClient.get('/admin/users')
        
        // Map backend 'name' field to frontend 'fullName'
        users.value = response.data.map(user => ({
          ...user,
          fullName: user.name || user.fullName // Backend uses 'name', frontend uses 'fullName'
        }))

        console.log(' Users loaded from API:', users.value)
      } catch (err) {
        console.error('Error loading users:', err)
        error.value = err.response?.data?.message || 'Failed to load users'
      } finally {
        loading.value = false
      }
    }

    // View user details
    function viewUser(user) {
      selectedUser.value = user
      showDetailsModal.value = true
    }

    // Clear filters
    function clearFilters() {
      searchQuery.value = ''
      filterRole.value = ''
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      users,
      isAdmin,
      searchQuery,
      filterRole,
      filteredUsers,
      adminCount,
      regularUserCount,
      showDetailsModal,
      selectedUser,
      loading,
      error,
      loadUsers,
      viewUser,
      clearFilters,
    }
  },
}
</script>

<style scoped>
.user-list-container {
  background-color: #f8f9fa;
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
</style>
