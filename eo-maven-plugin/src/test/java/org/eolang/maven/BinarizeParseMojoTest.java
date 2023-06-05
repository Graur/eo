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
package org.eolang.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import org.cactoos.text.TextOf;
import org.eolang.jucs.ClasspathSource;
import org.eolang.xax.XaxStory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link BinarizeParseMojo}.
 *
 * @since 0.1
 */
@Execution(ExecutionMode.CONCURRENT)
final class BinarizeParseMojoTest {

    @Test
    void parsesSimpleEoProgram(@TempDir final Path temp) throws Exception {
        final Path src = Paths.get("src/test/resources/org/eolang/maven/simple-rust.eo");
        final FakeMaven maven;
        synchronized (BinarizeParseMojoTest.class) {
            maven = new FakeMaven(temp).withProgram(src);
        }
        final Map<String, Path> res = maven
            .execute(new FakeMaven.BinarizeParse())
            .result();
        final String rust = String.format(
            "target/binarize/codes/%s0.rs",
            temp.resolve("target").toString()
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]", "x")
        );
        MatcherAssert.assertThat(
            res, Matchers.hasKey(rust)
        );
        MatcherAssert.assertThat(
            new TextOf(res.get(rust)).asString(),
            Matchers.stringContainsInOrder(
                "use rand::Rng;",
                "pub fn foo() -> i32 {",
                "  let mut rng = rand::thread_rng();",
                "  print!(\"Hello world\");",
                "  let i = rng.gen::<i32>();",
                "  i",
                "}"
            )
        );
    }

    @Test
    void binarizesTwiceRustProgram(@TempDir final Path temp) throws Exception {
        final Path src = Paths.get("src/test/resources/org/eolang/maven/twice-rust.eo");
        final FakeMaven maven;
        synchronized (BinarizeParseMojoTest.class) {
            maven = new FakeMaven(temp).withProgram(src);
        }
        final Map<String, Path> res = maven
            .execute(new FakeMaven.BinarizeParse())
            .result();
        final String prefix = temp.resolve("target").toString()
            .toLowerCase(Locale.ENGLISH)
            .replaceAll("[^a-z0-9]", "x");
        final String one = String.format(
            "target/binarize/codes/%s0.rs",
            prefix
        );
        final String two = String.format(
            "target/binarize/codes/%s1.rs",
            prefix
        );
        MatcherAssert.assertThat(
            res, Matchers.hasKey(one)
        );
        MatcherAssert.assertThat(
            res, Matchers.hasKey(two)
        );
        MatcherAssert.assertThat(
            new TextOf(res.get(one)).asString(),
            Matchers.stringContainsInOrder(
                "pub fn foo() -> i32 {",
                "print!(\"Hello world 1\");"
            )
        );
        MatcherAssert.assertThat(
            new TextOf(res.get(two)).asString(),
            Matchers.stringContainsInOrder(
                "pub fn foo() -> i32 {",
                "print!(\"Hello 大 2\");"
            )
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/maven/add_rust/", glob = "**.yaml")
    void createsDependenciesSection(final String yaml) {
        MatcherAssert.assertThat(
            new XaxStory(yaml),
            Matchers.is(true)
        );
    }

    @Test
    void createsCorrectRustProject(@TempDir final Path temp) throws Exception {
        final Path src = Paths.get("src/test/resources/org/eolang/maven/simple-rust.eo");
        final FakeMaven maven;
        synchronized (BinarizeParseMojoTest.class) {
            maven = new FakeMaven(temp)
                .withProgram(src)
                .withProgram(Paths.get("src/test/resources/org/eolang/maven/twice-rust.eo"));
        }
        final Map<String, Path> res = maven
            .execute(new FakeMaven.BinarizeParse())
            .result();
        final String prefix = temp.resolve("target").toString()
            .toLowerCase(Locale.ENGLISH)
            .replaceAll("[^a-z0-9]", "x");
        final String cargo = "target/Lib/Cargo.toml";
        final String lib = "target/Lib/src/lib.rs";
        final String module = String.format(
            "target/Lib/src/%s1.rs",
            prefix
        );
        MatcherAssert.assertThat(
            res, Matchers.hasKey(cargo)
        );
        MatcherAssert.assertThat(
            res, Matchers.hasKey(lib)
        );
        MatcherAssert.assertThat(
            res, Matchers.hasKey(module)
        );
        MatcherAssert.assertThat(
            new TextOf(res.get(cargo)).asString(),
            Matchers.stringContainsInOrder(
                "[lib]",
                "crate-type = [\"cdylib\"]",
                "[dependencies]",
                "jni = \"0.21.1\""
            )
        );
        MatcherAssert.assertThat(
            new TextOf(res.get(cargo)).asString(),
            Matchers.stringContainsInOrder(
                "[dependencies]",
                "rand = \"0.5.5\""
            )
        );
    }

}
