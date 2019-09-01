CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
    ON dbo.ORDERR
    FOR UPDATE
    AS
    BEGIN
		declare @id int
		declare @twop int
		declare @kursor cursor

		set @kursor = cursor for 
		select i.id, i.twoPercent
		from inserted i
		where i.state = 'arrived'

		open @kursor

		fetch next from @kursor
		into @id, @twop

		while @@FETCH_STATUS = 0 
		begin
			declare @articleId int
			declare @count int
			declare @shopId int
			declare @price decimal(10, 3)
			declare @discount int
			declare @kursor2 cursor

			
			set @kursor2 = cursor for
			select oi.articleId, oi.count
			from OrderItem oi
			where oi.orderId = @id

			open @kursor2

			fetch next from @kursor2
			into @articleId, @count

			while @@FETCH_STATUS = 0
			begin
				select @price = a.price, @shopId = a.ShopId
				from Article a
				where a.Id = @articleId

				select @discount = s.discount
				from Shop s
				where s.id = @shopId

				insert into Transactionn default values

				declare @nextId int
				select @nextId = max(t.id) from Transactionn t

				declare @amount int = @count * (@price - @price*@discount/100)

				insert into TransactionSystem(id, Amount, ShopId, OrderId) values(@nextId, @amount - @amount*(5-@twop)/100, @shopId, @id)

				fetch next from @kursor2
				into @articleId,  @count
			end
			close @kursor2
			deallocate @kursor2

			fetch next from @kursor
			into @Id, @twop
		end

		close @kursor
		deallocate @kursor

    SET NOCOUNT ON
    END
