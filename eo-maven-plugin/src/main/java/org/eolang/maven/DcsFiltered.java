/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 Objectionary.com
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

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.maven.model.Dependency;

/**
 * Filtered dependencies.
 *
 * @since 0.28.11
 */
class DcsFiltered implements Dependencies {

    /**
     * Decorated.
     */
    private final Dependencies dependencies;

    /**
     * All filters.
     */
    private final Collection<Predicate<Dependency>> filters;

    /**
     * The main constructor.
     *
     * @param dependencies Decorated
     * @param filters All fiters
     */
    DcsFiltered(
        final Dependencies dependencies,
        final Collection<Predicate<Dependency>> filters
    ) {
        this.dependencies = dependencies;
        this.filters = filters;
    }

    @Override
    public Collection<Dependency> all() {
        return this.dependencies.all()
            .stream()
            .filter(this::filter)
            .collect(Collectors.toList());
    }

    /**
     * Apply all filters for Dependency.
     *
     * @param dependency Dependency
     * @return True if all filters were passed
     */
    private boolean filter(final Dependency dependency) {
        return this.filters.stream().allMatch(f -> f.test(dependency));
    }

    /**
     * Filters runtime dependency eo-runtime.
     *
     * @since 0.28.11
     */
    static final class NotRuntime implements Predicate<Dependency> {
        @Override
        public boolean test(final Dependency dependency) {
            return !(
                dependency.getGroupId().equals("org.eolang")
                    && dependency.getArtifactId().equals("eo-runtime")
                );
        }
    }

    /**
     * Filters all dependencies with the same group and artifact id.
     *
     * @since 0.28.11
     */
    static final class NotSame implements Predicate<Dependency> {

        /**
         * Dependency to check.
         */
        private final Dependency current;

        /**
         * The main constructor.
         *
         * @param current Dependency to check
         */
        NotSame(final Dependency current) {
            this.current = current;
        }

        @Override
        public boolean test(final Dependency dependency) {
            return !(
                dependency.getGroupId().equals(this.current.getGroupId())
                    && dependency.getArtifactId().equals(this.current.getArtifactId())
                );
        }
    }

    /**
     * Filters all test dependencies.
     *
     * @since 0.28.11
     */
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    static final class NotTesting implements Predicate<Dependency> {
        @Override
        public boolean test(final Dependency dependency) {
            return !dependency.getScope().contains("test");
        }
    }
}
