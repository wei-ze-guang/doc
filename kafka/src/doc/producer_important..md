## 生产者 重要参数说明  
```text

Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially transient error. Note that this retry is no different than if the client resent the record upon receiving the error. Produce requests will be failed before the number of retries has been exhausted 
if the timeout configured by delivery.timeout.ms expires first before successful acknowledgement. Users should generally prefer to leave this config unset and instead use delivery.timeout.ms to control retry behavior.Enabling idempotence requires this config value to be greater than 0. If conflicting configurations are set and idempotence is not explicitly enabled,
idempotence is disabled.Allowing retries while setting enable.idempotence to false and max.in.flight.requests.per.connection to greater than 1 will potentially change the ordering of records because if two batches are sent to a single partition, and the first fails and is retried but the second succeeds, then the records in the second batch may appear first. 
这段话主要讲解了 Kafka 生产者的重试机制、幂等性配置以及可能的行为影响。以下是通俗的总结：
重试机制：如果配置了重试（即 retries 设置为大于零的值），Kafka 生产者会在发送失败时重新发送消息，尤其是遇到可能是暂时性的错误时。但是，如果设置了 delivery.timeout.ms（消息确认超时时间），如果在超时前未成功确认消息发送，生产者会停止重试。
幂等性：如果启用了幂等性（enable.idempotence 设置为 true），重试机制会更安全，因为它会确保即使重试发送相同的消息，消息也不会重复发送。如果没有明确启用幂等性，重试就不会有这个保证。
可能的顺序问题：如果在没有启用幂等性的情况下，max.in.flight.requests.per.connection 设置为大于 1，并且启用了重试，可能会导致消息顺序错乱。举个例子，如果两个消息批次同时发往一个分区，第一个批次失败后重试，第二个批次成功，那么第二个批次的消息会先被处理，导致顺序出错。
```
### delivery.timeout.ms 是 Kafka 生产者的一个配置项，它表示 消息发送的最大等待时间，即从生产者发送消息开始，到收到 Kafka 集群的成功确认，最多允许的时间。默认2秒 -1表示无限 
- 使用场景：
控制超时：这个配置项可以用来控制生产者的容忍超时，防止消息在出现网络问题或集群负载高时无限期地等待。 与重试配置结合使用：如果你设置了 retries（重试次数）并且配置了 delivery.timeout.ms，生产者会在超时之前重试消息的发送。如果超过超时还没成功确认，消息会被认为发送失败。
- 如果启用了幂等性（enable.idempotence=true），Kafka 生产者会确保即使在发生重试的情况下，同一条消息也不会被重复发送。也就是说，即使发送过程中出现问题，重试发送的消息只会被处理一次，不会因为重试而导致重复的消息。
而如果没有启用幂等性（enable.idempotence=false），即使重试机制被启用，也无法保证同一条消息不会被重复发送。也就是说，重试时可能会发送相同的消息多次，导致重复数据的出现。 
### 如果没有启用幂等性，并且设置了 max.in.flight.requests.per.connection 大于 1，则在发生消息重试时，可能会导致消息顺序错乱。因为重试的消息（失败的批次）可能会在成功的消息之后被处理。
- 换句话说，这个超时时间优先于重试次数。即使重试机制已启用，如果超过 delivery.timeout.ms 设定的时间，生产者就会放弃重试，认为发送失败。
### 就是说就算你开启了重试，那么当一个消息发送失败的时候，你必须这个时间段内给我必须收到客户端的反馈也就是ack，如果没收到，我直接不理会 总结：
- 和request.timeout.ms 参数有点区别
- 超时后，不会再接受任何成功反馈。即使后续有请求成功得到确认，也无法“挽回”超时的失败状态。
- 在超时后，生产者会认为 该消息发送失败，并根据相关配置（比如重试次数）返回失败结果。  
---  

### delivery.timeout.ms 参数
作用：
- delivery.timeout.ms 设置了 发送消息成功或失败报告的最大时间，即从调用 send() 方法返回开始，到 Kafka 确认消息是否发送成功的最长时间。
- 它包括了：
  - 发送消息的延迟时间。
  - 等待来自 Kafka broker 的确认时间（如果期望有确认）。
  - 允许的重试次数和重试过程中可能发生的错误。
- 不当使用的后果：
- 如果 delivery.timeout.ms 设置得太短，可能会导致：
  - 过早报告发送失败：即使在消息未完全处理完之前，生产者就可能认为消息发送失败。
  - 重试还未完成就认为失败：如果设置的时间小于 request.timeout.ms 和 linger.ms 的总和，可能会导致生产者在重试还未完成时就认为消息发送失败，浪费重试机会。
  - 消息过早丢失：如果设置的时间过短，可能会让消息丢失或未及时发往 Kafka，尤其是当消息需要等待确认或重试时。
- 配置建议：
  - delivery.timeout.ms 的值应该 大于或等于 request.timeout.ms 和 linger.ms 的总和，以确保在允许的时间范围内完成消息发送和重试。
- 总结：
  - 设置 delivery.timeout.ms 的时间过短会导致生产者提前报告失败，影响消息的可靠性和重试机制的有效性。所以，建议根据网络环境、系统负载和消息处理的实际需求，合理调整此值。
### request.timeout.ms 是 Kafka 生产者的配置项，用于设置 生产者发送请求后等待 Kafka broker 响应的最大时间。
- 作用：
  - request.timeout.ms 控制生产者在发送请求之后，最多等待多久来接收到 Kafka broker 的响应。
  - 如果在这个时间内没有收到响应，生产者就会认为请求失败，并根据重试策略或超时策略做出处理。
- 具体细节：
  - 请求等待时间：生产者发送消息到 Kafka 后，会等待 Kafka broker 的确认响应（acknowledgment）。request.timeout.ms 设置的是最大等待时间，即生产者等待 Kafka broker 响应的上限。
超时后处理：如果在 request.timeout.ms 时间内没有收到响应，生产者会认为消息发送失败，并根据配置的 重试次数（retries） 或 失败处理策略 来决定是否重新发送消息。
- 不同于 delivery.timeout.ms：
request.timeout.ms 是设置发送请求后，生产者等待 Kafka 响应的时间。而 delivery.timeout.ms 是设置从调用 send() 到最终确认消息的总时间，包含了重试、发送延迟等各方面的时间。
- 配置建议：
  - 一般来说，request.timeout.ms 不应该设置得太短，尤其是在网络延迟较高或 Kafka broker 负载较重时。如果设置得太短，生产者可能在消息尚未成功处理之前就认为失败。
同时，request.timeout.ms 应该小于或等于 delivery.timeout.ms，因为 delivery.timeout.ms 是包括了重试的总超时时间。
- 总结：
request.timeout.ms 用于设置生产者请求消息时，等待 Kafka 响应的最大时间。如果在这段时间内没有收到响应，就会报告发送失败。  
---  
