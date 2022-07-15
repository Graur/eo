/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 Yegor Bugayenko
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
 * @checkstyle PackageNameCheck (4 lines)
 */
package EOorg.EOeolang;

import org.eolang.AtFree;
import org.eolang.Attr;
import org.eolang.Data;
import org.eolang.PhDefault;
import org.eolang.PhWith;
import org.eolang.Phi;
import org.eolang.XmirObject;

/**
 * ERROR.
 *
 * @since 0.22
 * @checkstyle TypeNameCheck (5 lines)
 */
@XmirObject(oname = "error")
public final class EOerror extends PhDefault {

    /**
     * Ctor.
     * @param sigma Sigma
     */
    public EOerror(final Phi sigma) {
        super(sigma);
        this.add("msg", new AtFree());
    }

    /**
     * Phi constructed with error.
     * @since 1.0
     */
    public static final class PhWithError implements Phi {

        /**
         * Constructed phi.
         */
        private final Phi phi;

        /**
         * Ctor.
         * @param msg Error message
         */
        public PhWithError(final String msg) {
            this.phi = new PhWith(
                new EOerror(Phi.Φ),
                "msg",
                new Data.ToPhi(
                    msg
                )
            );
        }

        /**
         * Ctor.
         * @param format Message format string
         * @param params Parameters
         */
        public PhWithError(final String format, final Object... params) {
            this(
                String.format(
                    format,
                    params
                )
            );
        }

        @Override
        public Phi copy() {
            return this.phi.copy();
        }

        @Override
        public void move(final Phi rho) {
            this.phi.move(rho);
        }

        @Override
        public Attr attr(final int pos) {
            return this.phi.attr(pos);
        }

        @Override
        public Attr attr(final String name) {
            return this.phi.attr(name);
        }

        @Override
        public String φTerm() {
            return this.phi.φTerm();
        }
    }

}
