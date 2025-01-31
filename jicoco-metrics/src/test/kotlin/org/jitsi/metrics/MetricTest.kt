/*
 * Copyright @ 2022 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jitsi.metrics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MetricTest : ShouldSpec() {
    private val namespace = "test"

    init {
        context("Creating any metric type") {
            context("with an empty name") {
                should("throw an exception") {
                    shouldThrow<IllegalStateException> { BooleanMetric("", "Help", namespace) }
                    shouldThrow<IllegalStateException> { CounterMetric("", "Help", namespace) }
                    shouldThrow<IllegalStateException> { DoubleGaugeMetric("", "Help", namespace) }
                    shouldThrow<IllegalStateException> { InfoMetric("", "Help", namespace, "val") }
                    shouldThrow<IllegalStateException> { LongGaugeMetric("", "Help", namespace) }
                }
            }
            context("with an empty help string") {
                should("throw an exception") {
                    shouldThrow<IllegalStateException> { BooleanMetric("name", "", namespace) }
                    shouldThrow<IllegalStateException> { CounterMetric("name", "", namespace) }
                    shouldThrow<IllegalStateException> { DoubleGaugeMetric("name", "", namespace) }
                    shouldThrow<IllegalStateException> { InfoMetric("name", "", namespace, "val") }
                    shouldThrow<IllegalStateException> { LongGaugeMetric("name", "", namespace) }
                }
            }
        }
        context("Creating a BooleanMetric") {
            context("with the default initial value") {
                with(BooleanMetric("testBoolean", "Help", namespace)) {
                    context("and affecting its value") {
                        should("return the correct value") {
                            get() shouldBe false
                            setAndGet(true) shouldBe true
                            set(false).also { get() shouldBe false }
                        }
                    }
                }
            }
            context("with an initial value of true") {
                with(BooleanMetric("testBoolean", "Help", namespace, true)) {
                    should("return true") { get() shouldBe true }
                }
            }
        }
        context("Creating a CounterMetric") {
            context("with the default initial value") {
                with(CounterMetric("testCounter", "Help", namespace)) {
                    context("and incrementing its value repeatedly") {
                        should("return the correct value") {
                            get() shouldBe 0
                            incAndGet() shouldBe 1
                            repeat(20) { inc() }
                            get() shouldBe 21
                        }
                    }
                    context("and decrementing its value") {
                        should("throw an exception") {
                            shouldThrow<IllegalArgumentException> { addAndGet(-1) }
                        }
                    }
                }
            }
            context("with a positive initial value") {
                val initialValue: Long = 50
                with(CounterMetric("testCounter", "Help", namespace, initialValue)) {
                    should("return the initial value") { get() shouldBe initialValue }
                }
            }
            context("with a negative initial value") {
                val initialValue: Long = -50
                should("throw an exception") {
                    shouldThrow<IllegalArgumentException> {
                        CounterMetric("testCounter", "Help", namespace, initialValue)
                    }
                }
            }
        }
        context("Creating a LongGaugeMetric") {
            context("with the default initial value") {
                with(LongGaugeMetric("testLongGauge", "Help", namespace)) {
                    context("and affecting its value repeatedly") {
                        should("return the correct value") {
                            get() shouldBe 0
                            inc().also { dec() }
                            get() shouldBe 0
                            decAndGet() shouldBe -1
                            incAndGet() shouldBe 0
                            addAndGet(50) shouldBe 50
                            set(42).also { get() shouldBe 42 }
                        }
                    }
                }
            }
            context("with a given initial value") {
                val initialValue: Long = -50
                with(LongGaugeMetric("testLongGauge", "Help", namespace, initialValue)) {
                    should("return the initial value") { get() shouldBe initialValue }
                }
            }
        }
        context("Creating an InfoMetric") {
            context("with a value different from its name") {
                val value = "testInfoValue"
                with(InfoMetric("testInfo", "Help", namespace, value)) {
                    should("return the correct value") { get() shouldBe value }
                }
            }
        }
    }
}
