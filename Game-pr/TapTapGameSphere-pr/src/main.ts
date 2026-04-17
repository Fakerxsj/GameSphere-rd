import { createApp } from 'vue'
import './style.css' // 如果有全局样式文件
import App from './App.vue'
import router from './router'
import pinia from './stores' // 确保 store/index.ts 导出的是 pinia 实例或 createPinia()
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

app.use(pinia as any)
app.use(router as any)
app.use(ElementPlus as any)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 关键：确保这里挂载的是 #app，与 index.html 中的 div id 一致
app.mount('#app')