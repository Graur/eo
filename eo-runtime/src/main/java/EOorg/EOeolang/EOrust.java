/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 * @checkstyle PackageNameCheck (8 lines)
 */
package EOorg.EOeolang;

import java.nio.file.Paths;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;
import org.eolang.AtComposite;
import org.eolang.AtFree;
import org.eolang.Data;
import org.eolang.PhDefault;
import org.eolang.Phi;
import org.eolang.XmirObject;

/**
 * Rust.
 *
 * @since 0.29
 * @todo #1326:90min Generate this file at compile phase
 *  instead of keep it here. It allows to call any
 *  rust insert depending on its attributes.
 * @checkstyle MethodNameCheck (100 lines)
 * @checkstyle LineLengthCheck (100 lines)
 * @checkstyle TypeNameCheck (5 lines)
 */
@XmirObject(oname = "rust")
public class EOrust extends PhDefault {
    static {
        final String lib;
        if (SystemUtils.IS_OS_WINDOWS) {
            lib = "common.dll";
        } else if (SystemUtils.IS_OS_LINUX) {
            lib = "libcommon.so";
        } else if (SystemUtils.IS_OS_MAC) {
            lib = "libcommon.dylib";
        } else {
            throw new NotImplementedException(
                String.format(
                    "Rust inserts are not supported by %s os. Only windows, linux and macos are allowed.",
                    System.getProperty("os.name")
                )
            );
        }
        System.load(
            Paths.get("target")
                .resolve("eo-test")
                .resolve("Lib")
                .resolve("target")
                .resolve("debug")
                .resolve(lib)
                .toAbsolutePath()
                .toString()
        );
    }

    /**
     * Ctor.
     * @param sigma Sigma
     */
    public EOrust(final Phi sigma) {
        super(sigma);
        this.add("code", new AtFree());
        this.add("params", new AtFree());
        this.add(
            "φ",
            new AtComposite(
                this,
                rho ->
                    new Data.ToPhi(1234L)
            )
        );
    }
}
