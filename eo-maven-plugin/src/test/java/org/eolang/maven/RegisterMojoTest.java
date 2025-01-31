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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import org.cactoos.io.ResourceOf;
import org.eolang.maven.util.Home;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link RegisterMojo}.
 *
 * @since 0.11
 */
final class RegisterMojoTest {
    /**
     * Parameter for source directory.
     */
    private static final String PARAM = "sourcesDir";

    /**
     * Source directory.
     */
    private static final String SOURCES = "src/eo";

    @Test
    void registersOkNames(@TempDir final Path temp) throws IOException {
        new Home(temp).save(
            new ResourceOf("org/eolang/maven/file-name/abc-def.eo"),
            Paths.get("src/eo/org/eolang/maven/abc-def.eo")
        );
        final FakeMaven maven = new FakeMaven(temp)
            .with(RegisterMojoTest.PARAM, temp.resolve(RegisterMojoTest.SOURCES).toFile())
            .execute(new FakeMaven.Register());
        MatcherAssert.assertThat(
            maven.foreign().getById("org.eolang.maven.abc-def").exists("id"),
            Matchers.is(true)
        );
    }

    @Test
    void failsWithDotNames(@TempDir final Path temp) throws IOException {
        new Home(temp).save(
            new ResourceOf("org/eolang/maven/file-name/.abc.eo"),
            Paths.get("src/eo/org/eolang/maven/.abc.eo")
        );
        final IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            () -> {
                new FakeMaven(temp)
                    .with(RegisterMojoTest.PARAM, temp.resolve(RegisterMojoTest.SOURCES).toFile())
                    .execute(new FakeMaven.Register());
            }
        );
        MatcherAssert.assertThat(
            exception.getCause().getCause().getMessage(),
            Matchers.containsString("Incorrect name found: '.abc.eo'")
        );
    }

    @Test
    void doesNotFailWhenNoStrictNames(@TempDir final Path temp) throws IOException {
        new Home(temp).save(
            new ResourceOf("org/eolang/maven/file-name/.abc.eo"),
            Paths.get("src/eo/org/eolang/maven/.abc.eo")
        );
        final FakeMaven maven = new FakeMaven(temp)
            .with(RegisterMojoTest.PARAM, temp.resolve(RegisterMojoTest.SOURCES).toFile())
            .with("strictFileNames", false)
            .execute(new FakeMaven.Register());
        MatcherAssert.assertThat(
            maven.foreign().getById("org.eolang.maven..abc").exists("id"),
            Matchers.is(true)
        );
    }

    @Test
    void registersInExternal(@TempDir final Path temp) throws IOException {
        new Home(temp).save(
            new ResourceOf("org/eolang/maven/file-name/abc-def.eo"),
            Paths.get("src/eo/org/eolang/maven/foo.eo")
        );
        final String name = "org.eolang.maven.foo";
        final FakeMaven maven = new FakeMaven(temp)
            .with(RegisterMojoTest.PARAM, temp.resolve(RegisterMojoTest.SOURCES).toFile())
            .with("withVersions", true)
            .execute(new FakeMaven.Register());
        MatcherAssert.assertThat(
            String.format(
                "Source object %s placed in %s should have been registered in external tojos but it didn't",
                name,
                RegisterMojoTest.SOURCES
            ),
            maven.external()
                .getById(name)
                .exists("id"),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "External and foreign tojos should not have the same status after registering because external should replace foreign but they didn't",
            maven.foreignTojos().status(),
            Matchers.not(
                Matchers.equalTo(
                    maven.externalTojos().status()
                )
            )
        );
    }

    @Test
    void doesNotRegisterInExternal(@TempDir final Path temp) throws IOException {
        new Home(temp).save(
            new ResourceOf("org/eolang/maven/file-name/abc-def.eo"),
            Paths.get("src/eo/org/eolang/maven/foo.eo")
        );
        final String name = "org.eolang.maven.foo";
        Assertions.assertThrows(
            NoSuchElementException.class,
            () -> new FakeMaven(temp)
                .with(RegisterMojoTest.PARAM, temp.resolve(RegisterMojoTest.SOURCES).toFile())
                .with("withVersions", false)
                .execute(new FakeMaven.Register())
                .external()
                .getById(name),
            String.format(
                "External tojos should not have contained %s because \"withVersions\" is FALSE, but they did",
                name
            )
        );
    }

    @Test
    void throwsExceptionInCaseSourceDirIsNotSet(@TempDir final Path temp) {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new FakeMaven(temp)
                .withoutDefaults()
                .execute(new FakeMaven.Register()),
            String.format(
                "sourcesDir should not be set and the %s should fail, but didn't",
                RegisterMojo.class
            )
        );
    }
}
