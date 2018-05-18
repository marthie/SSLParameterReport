const path = require('path');

module.exports = {
    mode: "production",
    entry: "./src/app.js",
    output: {
        path: path.resolve(__dirname, 'dist/js'),
        filename: "bundle.js"
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                include: path.join(__dirname, 'src'),
                use: {
                    loader: "babel-loader"
                }
            }
        ]
    },
    devtool: "cheap-module-eval-source-map"
};