<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>注册 GameSphere</h2>
      <el-form :model="registerForm" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="昵称" prefix-icon="Avatar" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="full-width" :loading="loading" @click="handleRegister">注册</el-button>
        </el-form-item>
        <div class="links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '@/api/auth';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);

const registerForm = reactive({
  username: '',
  password: '',
  nickname: ''
});

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
};

const handleRegister = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await register(registerForm);
        ElMessage.success('注册成功，请登录');
        router.push('/login');
      } catch (error) {
        console.error(error);
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped lang="scss">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #1b2838;
  background-image: radial-gradient(circle at 50% 50%, rgba(102, 192, 244, 0.1) 0%, transparent 50%);
}

.register-card {
  width: 400px;
  padding: 30px;
  background-color: #16202d;
  border: 1px solid #2a475e;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);

  h2 {
    text-align: center;
    margin-bottom: 30px;
    color: #c7d5e0;
    font-size: 24px;
    font-weight: 500;
  }

  .full-width {
    width: 100%;
    background: linear-gradient(135deg, #66c0f4 0%, #1a9fff 100%);
    border: none;
    font-weight: 500;

    &:hover {
      background: linear-gradient(135deg, #7dd3ff 0%, #3db3ff 100%);
      box-shadow: 0 4px 16px rgba(102, 192, 244, 0.4);
    }
  }

  .links {
    text-align: center;
    margin-top: 15px;

    a {
      color: #66c0f4;
      text-decoration: none;
      transition: color 0.3s ease;

      &:hover {
        color: #7dd3ff;
        text-decoration: underline;
      }
    }
  }

  :deep(.el-input__wrapper) {
    background-color: #1b2838;
    box-shadow: 0 0 0 1px #2a475e inset;

    &:hover {
      box-shadow: 0 0 0 1px #66c0f4 inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px #66c0f4 inset;
    }
  }

  :deep(.el-input__inner) {
    color: #c7d5e0;
  }

  :deep(.el-input__prefix-inner) {
    color: #66c0f4;
  }
}
</style>
