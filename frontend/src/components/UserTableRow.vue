<template>
  <tr>
    <td><code>{{ user.id }}</code></td>
    <td>
      <div class="d-flex align-items-center">
        <div class="avatar-small me-2">
          <i class="bi bi-person-circle"></i>
        </div>
        <strong>{{ user.fullName }}</strong>
      </div>
    </td>
    <td>{{ user.email }}</td>
    <td>{{ user.phone || 'N/A' }}</td>
    <td>
      <span
        class="badge"
        :class="user.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'"
      >
        <i
          class="bi me-1"
          :class="user.role === 'ADMIN' ? 'bi-shield-check' : 'bi-person'"
        ></i>
        {{ user.role }}
      </span>
    </td>
    <td>
      <small>{{ formatDate(user.createdAt) }}</small>
    </td>
    <td class="text-center">
      <div class="btn-group" role="group">
        <button
          class="btn btn-sm btn-outline-info"
          @click="$emit('view', user)"
          title="View Details"
        >
          <i class="bi bi-eye"></i>
        </button>
      </div>
    </td>
  </tr>
</template>

<script>
export default {
  name: 'UserTableRow',
  
  props: {
    user: {
      type: Object,
      required: true
    }
  },

  emits: ['view'],

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
.avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
  color: white;
  font-size: 1rem;
}
</style>
