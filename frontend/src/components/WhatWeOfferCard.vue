<template>
  <div class="col-md-6 col-lg-3">
    <div class="feature-box text-center h-100">
      <div class="feature-icon-wrapper mx-auto mb-3">
        <i :class="['bi', icon, iconClass]"></i>
      </div>
      <h5 class="mb-3">{{ title }}</h5>
      <p class="text-muted small">
        {{ description }}
      </p>
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
.feature-box {
  padding: 2rem 1rem;
  background: white;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
}

.feature-box:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  border-color: transparent;
}

.feature-icon-wrapper {
  width: 80px;
  height: 80px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-icon-wrapper i {
  font-size: 2rem;
}
</style>
