function F = ForceMatrix(PA,PB)
    %for i=1:length(PB(1))
    F = (PA-PB)./vecnorm(PA-PB).^3; %/vecnorm(PA-PB).^2
end