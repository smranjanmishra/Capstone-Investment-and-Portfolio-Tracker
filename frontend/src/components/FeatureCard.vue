<template>
  <div class="col-md-4">
    <div class="card h-100 text-center shadow-sm feature-card">
      <div class="card-body">
        <div class="feature-icon rounded-circle mx-auto mb-3" :class="iconClass">
          <i class="bi" :class="icon"></i>
        </div>
        <h5 class="card-title fw-bold">{{ title }}</h5>
        <p class="card-text text-muted">
          {{ description }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FeatureCard',
  
  props: {
    icon: {
      type: String,
      required: true
    },
    iconClass: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    },
    description: {
      type: String,
      required: true
    }
  }
}
</script>

<style scoped>
.feature-card {
  transition: all 0.3s ease;
  border: none;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}
</style>
