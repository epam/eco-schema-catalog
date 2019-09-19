const dotenv = require('dotenv');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
// const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

dotenv.config();

const base = {
  devServer: {
    port: process.env.PORT,
    historyApiFallback: true,
    clientLogLevel: 'warning',
    hot: true,
    inline: true,
    overlay: true,
    contentBase: path.resolve('dist'),
    proxy: {
      [`${process.env.BASE_HREF}/api`]: {
        target: process.env.TARGET_API,
        pathRewrite: {
          [`^${process.env.BASE_HREF}/api`]: '/api',
        },
        changeOrigin: true,
      },
    },
  },

  entry: {
    bundle: './src/client/index.js',
  },

  output: {
    filename: '[name].js',
    path: path.join(__dirname, './dist/public'),
    publicPath: `${process.env.BASE_HREF || '/'}`,
    sourceMapFilename: '[name].js.map',
  },

  devtool: 'source-map',

  plugins: [
    // new BundleAnalyzerPlugin(),
    new webpack.DefinePlugin({
      'process.env': {
        BASE_HREF: JSON.stringify(process.env.BASE_HREF),
        NODE_ENV: JSON.stringify(process.env.NODE_ENV),
      },
    }),
    new MiniCssExtractPlugin({
      filename: 'assets/bundle.css',
    }),
    new HtmlWebpackPlugin({
      template: `${__dirname}/src/client/index.ejs`,
      filename: 'index.html',
      gaUA: process.env.GA_UA,
      favicon: './src/client/assets/logo/data_hub_logo.png',
      inject: true,
    }),
    new CopyWebpackPlugin([
      {
        from: 'src/server/',
        to: '../',
      },
    ]),
  ],
  resolve: {
    extensions: ['.js', '.jsx', '.json'],
    alias: {
      styles: path.join(__dirname, './src/client/styles'),
      'react-eco-ui': path.join(__dirname, './src/client/components/common/index.js'),
    },
  },
  module: {
    rules: [
      {
        test: /\.png$/,
        loader: 'file-loader?name=/assets/img/[name].png',
      },
      {
        test: /\.(eot|ttf|woff|woff2|otf)$/,
        loader: 'file-loader?name=/assets/fonts/[name].[ext]',
      },
      {
        type: 'javascript/auto',
        test: /\.json$/,
        loader: 'file-loader?name=/config/[name].json',
      },
      {
        test: /\.svg$/,
        use: ['url-loader'],
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
      },
      {
        test: /\.jsx$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
      },
      {
        test: /\.(sa|sc|c)ss$/,
        use: [
          {
            loader: MiniCssExtractPlugin.loader,
            options: {
              publicPath: `${process.env.BASE_HREF || ''}`,
              hmr: process.env.NODE_ENV === 'development',
            },
          },
          'css-loader',
          'sass-loader',
        ],
      },
      {
        test: /\.(html)$/,
        loader: 'html-loader',
        options: {
          minimize: false,
          attrs: ['img:src', 'link:href'],
        },
      },
    ],
  },
};

module.exports = base;
