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
package org.eolang.maven.hash;

import java.util.ArrayList;

import org.cactoos.Scalar;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapEnvelope;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.text.Split;

/**
 * Commit hashes hash-table.
 * The keys - tags.
 * The values - compound hashes (7 chars)
 *
 * @since 0.29.5
 */
final public class ChsAsMap extends MapEnvelope<String, CommitHash> {

    /**
     * Cached text of table of hashes.
     */
    private static final ArrayList<Scalar<String>> CACHE = new ArrayList<>(0);

    /**
     * Ctor.
     */
    public ChsAsMap() {
        this(ChsAsMap.once());
    }

    /**
     * Ctor.
     * @param table Commit hashes table as string.
     */
    public ChsAsMap(final String table) {
        this(() -> table);
    }

    /**
     * Ctor.
     * @param table Commit hashes table.
     */
    public ChsAsMap(final Scalar<String> table) {
        super(
            new MapOf<>(
                new Mapped<>(
                    line -> {
                        final String[] split = line.asString().split("\\s+");
                        return new MapEntry<>(
                            split[1],
                            new ChCached(
                                new ChNarrow(
                                    new CommitHash.ChConstant(split[0])
                                )
                            )
                        );
                    },
                    new Split(table::value, "\n")
                )
            )
        );
    }

    /**
     * Load commit hashes table and cache it.
     *
     * @return Text with tags and hashes as scalar.
     */
    private static Scalar<String> once() {
        if (ChsAsMap.CACHE.isEmpty()) {
            ChsAsMap.CACHE.add(
                new Sticky<>(
                    new ChsAsText()::asString
                )
            );
        }
        return ChsAsMap.CACHE.get(0);
    }

    /**
     * Fake commit hashes hash-table.
     */
    final public static class Fake extends MapEnvelope<String, CommitHash> {
        /**
         * Ctor.
         */
        public Fake() {
            super(
                new ChsAsMap(
                    String.join(
                        "\n", 
                        "5fe5ad8d21dbe418038fa4c86e096fb037f290a9 0.23.15",
                        "15c85d7f8cffe15b0deba96e90bdac98a76293bb 0.23.17",
                        "4b19944d86058e3c81e558340a3a13bc335a2b48 0.23.19",
                        "0aa6875c40d099c3f670e93d4134b629566c5643 0.25.0",
                        "ff32e9ff70c2b3be75982757f4b0607dc37b258a 0.25.5",
                        "e0b783692ef749bb184244acb2401f551388a328 0.26.0",
                        "cc554ab82909eebbfdacd8a840f9cf42a99b64cf 0.27.0",
                        "00b60c7b2112cbad4e37ba96b162469a0e75f6df 0.27.2",
                        "6a70071580e95aeac104b2e48293d3dfe0669973 0.28.0",
                        "0c15066a2026cec69d613b709a301f1573f138ec 0.28.1",
                        "9b883935257bd59d1ba36240f7e213d4890df7ca 0.28.10",
                        "a7a4556bf1aa697324d805570f42d70affdddb75 0.28.14",
                        "54d83d4b1d28075ee623d58fd742eaa529febd3d 0.28.2",
                        "6c6269d1f9a1c81ffe641538f119fe4e12706cb3 0.28.4",
                        "9c9352890b5d30e1b89c9147e7c95a90c9b8709f 0.28.5",
                        "17f89293e5ae6115e9a0234b754b22918c11c602 0.28.6",
                        "5f82cc1edffad67bf4ba816610191403eb18af5d 0.28.7",
                        "be83d9adda4b7c9e670e625fe951c80f3ead4177 0.28.9"
                    )
                )
            );
        }
    }
}
