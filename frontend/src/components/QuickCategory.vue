<template>
  <div class="col-6 col-md-3">
    <RouterLink :to="to" class="category-card">
      <div class="category-icon" :class="iconClass">
        <i class="bi" :class="icon"></i>
      </div>
      <h6 class="mt-2 mb-0">{{ title }}</h6>
    </RouterLink>
  </div>
</template>

<script>
import { RouterLink } from 'vue-router'

export default {
  name: 'QuickCategory',
  
  components: {
    RouterLink
  },

  props: {
    to: {
      type: String,
      required: true
    },
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
    }
  }
}
</script>

<style scoped>
.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1.5rem 1rem;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  text-decoration: none;
  color: #333;
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: #00d09c;
}

.category-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
}

.category-card h6 {
  font-size: 0.9rem;
  font-weight: 600;
  color: #444;
}
</style>
