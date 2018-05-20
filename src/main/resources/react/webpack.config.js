const path = require('path');

module.exports = {
    mode: "development",
    entry: {
        "app": "./src/ssl-report/app.js",
        "lib": "./src/lib.js",
        "styles-libs": "./src/styles-libs.js"
    },

    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                include: path.join(__dirname, 'src'),
                use: {
                    loader: "babel-loader"
                }
            },{
                test: /\.css$/,
                use: [
                    "style-loader", "css-loader"
                ]
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                use: {
                    loader: "file-loader",
                    options: {
                        name: "assets/[name].[ext]"
                    }
                }
            }
        ]
    },
    output: {
        path: path.resolve(__dirname, "dist"),
        filename: "[name].js"
    },
    devtool: "cheap-module-eval-source-map"
};