export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#071013',
        panel: '#0d1b1e',
        line: '#20353a',
        neon: '#38f2af',
        cyan: '#48d7ff',
        amber: '#f8c14a',
        danger: '#ff5876'
      },
      boxShadow: {
        glow: '0 0 40px rgba(56, 242, 175, 0.16)'
      }
    }
  },
  plugins: []
};
