<template>
  <div class="col-md-4">
    <div class="value-card h-100">
      <div class="card border-0 shadow-sm h-100">
        <div class="card-body p-4">
          <div class="value-icon mb-3">
            <i :class="['bi', icon, 'display-5', iconClass]"></i>
          </div>
          <h5 class="mb-3">{{ title }}</h5>
          <p class="text-muted mb-0">
            {{ description }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    required: true
  },
  iconClass: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.value-card {
  transition: all 0.3s ease;
}

.value-card:hover {
  transform: translateY(-10px);
}

.value-card .card {
  transition: all 0.3s ease;
}

.value-card:hover .card {
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.value-icon {
  width: 80px;
  height: 80px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
