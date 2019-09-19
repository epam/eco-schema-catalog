module.exports = {
  presets: ['@babel/preset-env', '@babel/react'],
  plugins: [
    ['@babel/plugin-transform-modules-commonjs'],
    ['@babel/plugin-proposal-class-properties', { spec: true }],
    ['@babel/plugin-proposal-object-rest-spread'],
    ['@babel/plugin-transform-runtime'],
  ],
};
