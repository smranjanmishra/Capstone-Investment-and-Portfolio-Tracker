<template>
  <div
    v-if="show"
    class="modal fade show d-block"
    tabindex="-1"
    style="background: rgba(0, 0, 0, 0.5)"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            <i class="bi bi-person-circle me-2"></i>
            User Details
          </h5>
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>
        <div class="modal-body" v-if="user">
          <div class="text-center mb-4">
            <div class="avatar-circle mx-auto mb-3">
              <i class="bi bi-person-fill"></i>
            </div>
            <h4 class="fw-bold">{{ user.fullName }}</h4>
            <span
              class="badge"
              :class="user.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'"
            >
              {{ user.role }}
            </span>
          </div>
          <div class="row g-3">
            <div class="col-6">
              <label class="text-muted small">User ID</label>
              <p class="fw-bold mb-0"><code>{{ user.id }}</code></p>
            </div>
            <div class="col-6">
              <label class="text-muted small">Email</label>
              <p class="fw-bold mb-0">{{ user.email }}</p>
            </div>
            <div class="col-6">
              <label class="text-muted small">Phone</label>
              <p class="fw-bold mb-0">{{ user.phone || 'N/A' }}</p>
            </div>
            <div class="col-6">
              <label class="text-muted small">Registered</label>
              <p class="fw-bold mb-0">
                <small>{{ formatDate(user.createdAt) }}</small>
              </p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="$emit('close')">
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserDetailsModal',
  
  props: {
    show: {
      type: Boolean,
      required: true
    },
    user: {
      type: Object,
      default: null
    }
  },

  emits: ['close'],

  methods: {
    // Format date
    formatDate(dateString) {
      if (!dateString) return 'N/A'
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    }
  }
}
</script>

<style scoped>
.avatar-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
  color: white;
  font-size: 3rem;
}

.modal.show {
  display: block;
}
</style>
