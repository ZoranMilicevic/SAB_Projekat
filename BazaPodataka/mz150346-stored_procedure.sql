CREATE PROCEDURE dbo.SP_FINAL_PRICE 
    @orderId int  
AS
    UPDATE dbo.Orderr
	SET FINALPRICE = dbo.F_FINAL_PRICE(@orderId) * (100-dbo.TWO_PERCENT(@orderId))/100,
	TwoPercent = dbo.TWO_PERCENT(@orderId)
	WHERE ID = @orderId  
RETURN 0 
go


CREATE FUNCTION dbo.F_FINAL_PRICE
(
    @orderId int
)
RETURNS DECIMAL(10, 3)
AS 
BEGIN
	RETURN(
		SELECT SUM(oi.count * (a.price - a.price*s.discount/100)) from OrderItem oi, Article a, Shop s, dbo.Orderr o
		where oi.orderId = @orderId and oi.ArticleId = a.Id and a.shopId = s.id      
	) 
END
GO


CREATE FUNCTION dbo.TWO_PERCENT
(
    @orderId int
)
RETURNS INTEGER
AS 
BEGIN
	declare @suma integer
	declare @buyerId integer

	select @buyerId = o.buyerId
	from dbo.Orderr o
	where o.id = @orderId

	select @suma = sum(o.finalPrice) 
	from dbo.Orderr o 
	where o.BuyerId = @buyerId and o.SentTime > DATEADD(dd, -30, CURRENT_TIMESTAMP)

	if @suma>10000
	begin 
		return 2
	end
	return 0
END
GO

