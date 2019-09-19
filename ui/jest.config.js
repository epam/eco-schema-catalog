module.exports = {
  transform: {
    '^.+\\.js$': 'babel-jest',
    '\\.(css|less|scss|sass|svg)$': '<rootDir>/__mocks__/style.js',
  },
  transformIgnorePatterns: [
    '/node_modules/(?!lodash-es).+\\.js$',
  ],
  moduleNameMapper: {
    '^.+\\.scss$': 'identity-obj-proxy',
  },
  roots: ['<rootDir>/src/'],
  moduleFileExtensions: ['js', 'jsx'],
  setupFiles: ['<rootDir>/src/client/testSetup.js'],
  collectCoverage: true,
};
